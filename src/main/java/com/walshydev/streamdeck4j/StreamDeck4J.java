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
import com.walshydev.streamdeck4j.events.ActionDisappearedEvent;
import com.walshydev.streamdeck4j.events.ApplicationLaunchedEvent;
import com.walshydev.streamdeck4j.events.ApplicationTerminatedEvent;
import com.walshydev.streamdeck4j.events.DeviceConnectedEvent;
import com.walshydev.streamdeck4j.events.DeviceDisconnectedEvent;
import com.walshydev.streamdeck4j.events.Event;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.events.KeyUpEvent;
import com.walshydev.streamdeck4j.events.SentToPluginEvent;
import com.walshydev.streamdeck4j.events.TitleParametersDidChangeEvent;
import com.walshydev.streamdeck4j.hooks.EventListener;
import com.walshydev.streamdeck4j.info.Alignment;
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
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
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
    private boolean registered;

    /**
     * Connects to the websocket for Stream Deck
     *
     * @param args Program arguments, such as Websocket port, plugin UUID, etc.
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
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }

                        toSend = new ActionDisappearedEvent(
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
                    case "titleParametersDidChange":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }

                        toSend = new TitleParametersDidChangeEvent(
                            jsonObject.get("context").getAsString(),
                            jsonObject.get("action").getAsString(),
                            jsonObject.get("device").getAsString(),

                            // Payload data
                            payload.get("settings").getAsJsonObject(),
                            gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                            payload.get("state").getAsInt(),
                            payload.get("title").getAsString(),
                            getFont(payload.get("titleParameters").getAsJsonObject()),
                            Color.decode(payload.get("titleColor").getAsString()),
                            Alignment.valueOf(payload.get("titleAlignment").getAsString().toUpperCase())
                        );
                        break;
                    case "deviceDidConnect":
                        toSend = new DeviceConnectedEvent(jsonObject.get("device").getAsString());
                        break;
                    case "deviceDidDisconnect":
                        toSend = new DeviceDisconnectedEvent(jsonObject.get("device").getAsString());
                        break;
                    case "applicationDidLaunch":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }
                        toSend = new ApplicationLaunchedEvent(payload.get("application").getAsString());
                        break;
                    case "applicationDidTerminate":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }
                        toSend = new ApplicationTerminatedEvent(payload.get("application").getAsString());
                        break;
                    case "sendToPlugin":
                        if (payload == null) {
                            logger.error("Invalid JSON for {}", event);
                            break;
                        }
                        toSend = new SentToPluginEvent(
                            jsonObject.get("context").getAsString(),
                            jsonObject.get("action").getAsString(),
                            payload
                        );
                        break;
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

    private void sendEvent(@Nonnull SDEvent event, @Nullable JsonObject payload, @Nullable String context) {
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

    private void sendPayload(@Nonnull JsonObject object) {
        logger.trace("-- SENT: " + object.toString());
        ws.sendBinary(object.toString().getBytes());
    }

    @SuppressWarnings("unchecked")
    private Font getFont(JsonObject titleParameters) {
        int fontSize = titleParameters.get("fontSize").getAsInt();
        if (fontSize > 18 || fontSize < 6)
            throw new IllegalArgumentException("Received font size value outside of the range 6-18... is this ever meant to happen?");
        Font f = new Font(
            titleParameters.get("fontFamily").getAsString(),
            getFontStyle(titleParameters.get("fontStyle").getAsString()),
            fontSize);
        if (titleParameters.get("fontUnderline").getAsBoolean()) {
            Map<TextAttribute, Integer> attributes = (HashMap<TextAttribute, Integer>) f.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            f = f.deriveFont(attributes);
        }
        return f;
    }

    private int getFontStyle(String fontStyle) {
        fontStyle = fontStyle.toLowerCase();
        if (fontStyle.contains("bold") && fontStyle.contains("italic"))
            return Font.BOLD + Font.ITALIC;
        else if (fontStyle.contains("bold"))
            return Font.BOLD;
        else if (fontStyle.contains("italic"))
            return Font.ITALIC;
        else
            return Font.PLAIN;
    }

    /////////////////////////
    // Public methods
    /////////////////////////

    /**
     * Adds an event listener to the plugin
     *
     * @param eventListener The listener to add
     */
    public void addListener(@Nonnull EventListener eventListener) {
        this.listeners.add(eventListener);
    }

    /**
     * Get the {@link Application} info, this contains the language, platform and version being used.
     *
     * @return {@link Application} containing application info.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * A collection of the {@link Device}s being used.
     *
     * @return A collection of the {@link Device}s being used.
     */
    public Set<Device> getDevices() {
        return this.devices;
    }

    /**
     * Opens a URL on the PC the Stream Deck is connected to.
     *
     * @param url The URL to open
     */
    public void openURL(@Nonnull URL url) {
        logger.trace("openURL(url)");
        JsonObject payload = new JsonObject();
        payload.addProperty("url", url.toString());

        sendEvent(SDEvent.OPEN_URL, payload);
    }

    /**
     * Sets the title of a specific button on the Stream Deck
     *
     * @param context     The unique identifier for the button
     * @param title       The new title for the button
     * @param destination The destination for the event (Hardware, Software or Both)
     */
    public void setTitle(@Nonnull String context, @Nonnull String title, @Nonnull Destination destination) {
        logger.trace("setTitle(context, title, destination)");
        JsonObject obj = new JsonObject();
        obj.addProperty("title", title);
        obj.addProperty("target", destination.ordinal());
        sendEvent(SDEvent.SET_TITLE, obj, context);
    }

    /**
     * Sets the image of a specific button on the Stream Deck
     *
     * @param context     The unique identifier for the button
     * @param image       The image to display on the button
     * @param destination The destination for the event (Hardware, Software or Both)
     */
    public void setImage(@Nonnull String context, BufferedImage image, Destination destination) {
        logger.trace("setImage(context, image, destination)");
        setImage(context, image, "png", destination);
    }

    /**
     * Sets the image of a specific button on the Stream Deck
     *
     * @param context     The unique identifier for the button
     * @param image       The image to display on the button
     * @param type        The image's file type (jpg, png, etc.)
     * @param destination The destination for the event (Hardware, Software or Both)
     */
    public void setImage(@Nonnull String context, BufferedImage image, String type, Destination destination) {
        logger.trace("setImage(context, image, type, destination)");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, baos);
        } catch (Exception e) {
            logger.error("Failed to encode image!", e);
            return;
        }
        byte[] imageBytes = baos.toByteArray();
        setImage(context, Base64.getEncoder().encodeToString(imageBytes), type, destination);
    }

    /**
     * Sets the image of a specific button on the Stream Deck
     * Note that the Base64 string of the image does not require the 'data:image/{type};base64' at the beginning of it.
     * This is appended in the function itself.
     *
     * @param context       The unique identifier of the button
     * @param base64Encoded The Base64-encoded string of the image to display on the button
     * @param type          The image's file type (jpg, png, etc.)
     * @param destination   The destination for the event (Hardware, Software or Both)
     */
    public void setImage(@Nonnull String context, String base64Encoded, String type, Destination destination) {
        logger.trace("setImage(context, base64Encoded, type, destination)");
        if (!base64Encoded.startsWith("data:image")) {
            base64Encoded = "data:image/" + type + ";base64," + base64Encoded;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("image", base64Encoded);
        payload.addProperty("target", destination.ordinal());
        sendEvent(SDEvent.SET_IMAGE, payload, context);
    }

    /**
     * Shows an alert on the Stream Deck
     *
     * @param context The unique identifier of the button to show the alert on.
     */
    public void showAlert(@Nonnull String context) {
        logger.trace("showAlert(context)");
        sendEvent(SDEvent.SHOW_ALERT, null, context);
    }

    /**
     * Shows a checkmark on a specific button on the Stream Deck
     *
     * @param context The unique identifier of the button to show the checkmark on
     */
    public void showOk(@Nonnull String context) {
        logger.trace("showOk(context)");
        sendEvent(SDEvent.SHOW_OK, null, context);
    }

    /**
     * Saves persistent data for the instance of the action.
     * This data is found through events such as keyDown, keyUp, willAppear, etc.
     * You can get it by calling `getSettings()` on the event inside an EventListener.
     * ELI5: saves some data to the stream deck so if you restart it, it's still there
     *
     * @param context    The unique identifier of the button with the action you want to change data for
     * @param dataToSave The data you want to save (in JSON-format)
     */
    public void setSettings(@Nonnull String context, @Nonnull JsonObject dataToSave) {
        logger.trace("setSettings(context, dataToSave)");
        sendEvent(SDEvent.SET_SETTINGS, dataToSave, context);
    }

    /**
     * Allows you to change the state of an action that has multiple states
     *
     * @param context The unique identifier of the button with the action you want to change
     * @param state   A 0-based integer for the state (0, 1, 2, etc.)
     */
    public void setState(@Nonnull String context, int state) {
        logger.trace("setState(context, state)");
        JsonObject payload = new JsonObject();
        payload.addProperty("state", state);
        sendEvent(SDEvent.SET_STATE, payload, context);
    }

    /**
     * Sends an action to the Property Inspector.
     *
     * @param context The unique identifier of the button with the action you want to send.
     * @param action  The unique identifier of the action you want to send.
     * @param payload The JSON object that will be received by the Property Inspector.
     */
    public void sendToPropertyInspector(@Nonnull String context, @Nonnull String action, @Nonnull JsonObject payload) {
        logger.trace("sendToPropertyInspector(context, action, payload)");
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", SDEvent.SEND_TO_PROPERTY_INSPECTOR.getName());
        eventJson.addProperty("context", context);
        eventJson.addProperty("action", action);
        eventJson.add("payload", payload);

        sendPayload(eventJson);
    }

    /**
     * Changes to a profile on the Stream Deck based on the profile's name
     *
     * @param deviceId The unique ID for the Stream Deck. This value changes each time you relaunch the Stream Deck app.
     * @param profile  The name of the profile you want to switch to.
     */
    public void switchToProfile(@Nonnull String deviceId, @Nonnull String profile) {
        logger.trace("switchToProfile(context, deviceId, payload)");
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", SDEvent.SWITCH_TO_PROFILE.getName());
        eventJson.addProperty("context", pluginUUID.toString().toUpperCase());
        eventJson.addProperty("device", deviceId);

        JsonObject payload = new JsonObject();
        payload.addProperty("profile", profile);

        eventJson.add("payload", payload);

        sendPayload(eventJson);
    }
}
