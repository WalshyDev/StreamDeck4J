package com.walshydev.streamdeck4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.DeviceConnectedEvent;
import com.walshydev.streamdeck4j.events.Event;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.events.KeyUpEvent;
import com.walshydev.streamdeck4j.hooks.EventListener;
import com.walshydev.streamdeck4j.info.Application;
import com.walshydev.streamdeck4j.info.Coordinates;
import com.walshydev.streamdeck4j.info.Destination;
import com.walshydev.streamdeck4j.info.Device;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StreamDeck4J {

    private static final Logger logger = LoggerFactory.getLogger(StreamDeck4J.class);

    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();
    private final Set<EventListener> listeners = new HashSet<>();

    private CommandLine cmd;
    private WebSocket ws;
    private UUID pluginUUID;
    private Application application;
    private Set<Device> devices;
    private boolean registered = false;

    /**
     * Pass in the program arguments
     *
     * @param args
     */
    public void connect(String[] args) {
        logger.trace("Hello, World!");
        logger.trace("Parsing arguments - " + Arrays.toString(args));
        parseArguments(args);
        logger.trace("Parsed arguments - " + Arrays.toString(cmd.getOptions()));

        logger.debug("Connecting to websocket");
        connectToWebsocket();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ws.isOpen())
                ws.sendClose();
        }));
    }

    //////////////////////////
    // INTERNALS
    //////////////////////////
    private void parseArguments(String[] args) {
        Options options = new Options();
        options.addOption("port", true, "The websocket port to connect with");
        options.addOption("pluginUUID", true, "The plugin UUID");
        options.addOption("registerEvent", true, "The registration event");
        options.addOption("info", true, "JSON info");

        CommandLineParser parser = new DefaultParser();
        try {
            this.cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Failed to parse args! Report on GitHub with stacktrace!", e);
            System.exit(1);
        }
    }

    private void connectToWebsocket() {
        if (!cmd.hasOption("port")) {
            logger.error("Was not provided a port! Is this lib up to date?");
            System.exit(1);
        }

        this.pluginUUID = UUID.fromString(cmd.getOptionValue("pluginUUID"));

        // Parse the info argument passed in, from this we can get the application info and also what devices are
        // connected
        JsonObject infoObj = new JsonParser().parse(cmd.getOptionValue("info")).getAsJsonObject();
        this.application = gson.fromJson(infoObj.get("application").getAsJsonObject(), Application.class);
        Type devicesType = new TypeToken<Set<Device>>() {
        }.getType();
        this.devices = gson.fromJson(infoObj.get("devices").getAsJsonArray(), devicesType);

        try {
            logger.debug("Creating websocket at 'ws://localhost:{}'", cmd.getOptionValue("port"));
            ws = new WebSocketFactory().createSocket("ws://localhost:" + cmd.getOptionValue("port"));
        } catch (IOException e) {
            logger.error("Failed to create websocket!", e);
        }

        ws.addListener(new WebSocketAdapter() {
            public void handleCallbackError(WebSocket websocket, Throwable cause) {
                logger.error("Something bad happened on the WebSocket!", cause);
            }

            public void onStateChanged(WebSocket websocket, WebSocketState newState) {
                logger.trace("onStateChanged {}", newState.toString());
            }

            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                logger.trace("onConnected {}", headers.toString());
                if (!registered) {
                    logger.trace("Registering - " + pluginUUID.toString().toUpperCase());
                    JsonObject registerJson = new JsonObject();
                    registerJson.addProperty("event", "registerPlugin");
                    registerJson.addProperty("uuid", pluginUUID.toString().toUpperCase());
                    sendPayload(registerJson);
                    registered = true;
                }
                logger.info("Connected to the WebSocket!");
            }

            public void onConnectError(WebSocket websocket, WebSocketException exception) {
                logger.trace("onConnectError", exception);
            }

            public void onDisconnected(
                WebSocket websocket,
                WebSocketFrame serverCloseFrame,
                WebSocketFrame clientCloseFrame,
                boolean closedByServer
            ) {
                logger.debug(
                    "onDisconnected - Server: {}, Code: {}, Reason: {}",
                    closedByServer,
                    closedByServer ? serverCloseFrame.getCloseCode() : clientCloseFrame.getCloseCode(),
                    closedByServer ? serverCloseFrame.getCloseReason() : clientCloseFrame.getCloseReason()
                );
                logger.info(
                    "{} disconnected from WebSocket - {}",
                    closedByServer ? "Server" : "Client",
                    closedByServer ? serverCloseFrame.getCloseReason() : clientCloseFrame.getCloseReason()
                );
            }

            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) {
                logger.trace("onCloseFrame - Code: {}, Reason: {}", frame.getCloseCode(), frame.getCloseReason());
            }

            public void onTextMessage(WebSocket websocket, String text) {
                logger.trace("onTextMessage - {}", text);

                // We should be safe to assume this, if we get anything else at least we have the TRACE log to figure
                // out what
                JsonObject jsonObject = parser.parse(text).getAsJsonObject();

                if (!jsonObject.has("event")) {
                    logger.error("Received JSON but without an event field! JSON: " + jsonObject.toString());
                    return;
                }

                final String event = jsonObject.get("event").getAsString();

                JsonObject payload = null;
                if (jsonObject.has("payload"))
                    payload = jsonObject.get("payload").getAsJsonObject();

                // All current known receive events:
                //[X] keyDown
                //[X] keyUp
                //[X] willAppear
                //willDisappear
                //titleParametersDidChange
                //[X] deviceDidConnect
                //deviceDidDisconnect
                //applicationDidLaunch
                //applicationDidTerminate

                Event toSend = null;

                switch (event) {
                    case "keyDown":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }

                        toSend = new KeyDownEvent(
                            jsonObject.get("context").getAsString(),
                            jsonObject.get("action").getAsString(),
                            jsonObject.get("device").getAsString(),
                            // Payload data
                            payload.get("settings").getAsJsonObject(),
                            gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                            payload.has("state") ? payload.get("state").getAsInt() : 0,
                            payload.has("userDesiredState") ? payload.get("userDesiredState").getAsInt() : 0,
                            payload.get("isInMultiAction").getAsBoolean()
                        );

                        break;
                    case "keyUp":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }

                        toSend = new KeyUpEvent(
                            jsonObject.get("context").getAsString(),
                            jsonObject.get("action").getAsString(),
                            jsonObject.get("device").getAsString(),
                            // Payload data
                            payload.get("settings").getAsJsonObject(),
                            gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                            payload.has("state") ? payload.get("state").getAsInt() : 0,
                            payload.has("userDesiredState") ? payload.get("userDesiredState").getAsInt() : 0,
                            payload.get("isInMultiAction").getAsBoolean()
                        );
                        break;
                    case "willAppear":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }

                        toSend = new ActionAppearedEvent(
                            jsonObject.get("context").getAsString(),
                            jsonObject.get("action").getAsString(),
                            jsonObject.get("device").getAsString(),
                            // Payload data
                            payload.get("settings").getAsJsonObject(),
                            gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                            payload.has("state") ? payload.get("state").getAsInt() : 0,
                            payload.get("isInMultiAction").getAsBoolean()
                        );
                        break;
                    case "willDisappear":
                    case "titleParametersDidChange":
                        logger.warn("'{}' isn't implemented yet! Sorry!", jsonObject.get("event").getAsString());
                        break;
                    case "deviceDidConnect":
                        toSend = new DeviceConnectedEvent(null, jsonObject.get("device").getAsString());
                        break;
                    case "deviceDidDisconnect":
                    case "applicationDidLaunch":
                    case "applicationDidTerminate":
                        logger.warn("'{}' isn't implemented yet! Sorry!", jsonObject.get("event").getAsString());
                    default:
                        logger.warn(
                            "Received unknown event! '{}' - Ignoring for now!",
                            jsonObject.get("event").getAsString()
                        );
                        break;
                }

                if (toSend == null)
                    return;

                logger.trace("Firing event: {} to {} listeners.", toSend.getClass().getSimpleName(), listeners.size());

                for (EventListener listener : listeners)
                    listener.onEvent(toSend);
            }

            public void onError(WebSocket websocket, WebSocketException cause) {
                logger.trace("onError - {}", cause.getError().name(), cause);
            }

            public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) {
                logger.trace("onFrameError - {}", cause.getError().name(), cause);
            }

            public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) {
                logger.trace("onMessageError - {}", cause.getError().name(), cause);
            }

            public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) {
                logger.trace("onTextMessageError - {}", cause.getError().name(), cause);
            }

            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) {
                logger.trace("onSendError - {}", cause.getError().name(), cause);
            }

            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
                logger.trace("onUnexpectedError - {}", cause.getError().name(), cause);
            }
        });

        ws.connectAsynchronously();
    }

    private void sendEvent(@Nonnull SDEvent event, @Nonnull JsonObject payload) {
        logger.trace("sendEvent(SDEvent, payload)");
        if (event.hasContext())
            throw new IllegalArgumentException("No context passed but " + event.getName() + " needs one!");

        sendEvent(event, payload, null);
    }

    private void sendEvent(@Nonnull SDEvent event, @Nonnull JsonObject payload, @Nullable String context) {
        logger.trace("sendEvent(SDEvent, payload, context)");
        if (context == null && event.hasContext())
            throw new IllegalArgumentException("No context passed but " + event.getName() + " needs one!");

        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", event.getName());
        if (event.hasContext())
            eventJson.addProperty("context", context);

        eventJson.add("payload", payload);

        logger.trace("Sending payload - {}", eventJson.toString());
        sendPayload(eventJson);
    }

    private void sendPayload(JsonObject object) {
        logger.trace("-- SENT: " + object.toString());
        ws.sendBinary(object.toString().getBytes());
    }

    /////////////////////////
    // Public methods
    /////////////////////////
    public void addListener(EventListener eventListener) {
        this.listeners.add(eventListener);
    }

    public void openURL(URL url) {
        logger.trace("openURL(url)");
        JsonObject payload = new JsonObject();
        payload.addProperty("url", url.toString());

        sendEvent(SDEvent.OPEN_URL, payload);
    }

    public void setTitle(String title, Destination destination, String context) {
        logger.trace("setTitle(title, destination, context)");
        JsonObject obj = new JsonObject();
        obj.addProperty("title", title);
        obj.addProperty("target", destination.ordinal());
        sendEvent(SDEvent.SET_TITLE, obj, context);
    }
}
