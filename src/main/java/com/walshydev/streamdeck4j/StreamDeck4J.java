package com.walshydev.streamdeck4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StreamDeck4J {

    private static final Logger logger = LoggerFactory.getLogger(StreamDeck4J.class);

    private final Gson gson = new Gson();

    private CommandLine cmd;
    private WebSocket ws;
    private UUID pluginUUID;
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

    public void openURL(URL url) {
        JsonObject obj = new JsonObject();
        obj.addProperty("event", "openUrl");

        JsonObject payload = new JsonObject();
        payload.addProperty("url", url.toString());
        obj.add("payload", payload);

        sendPayload(obj);
    }

    public void setTitle(String title, Destination destination) {
        JsonObject obj = new JsonObject();
        obj.addProperty("title", title);
        obj.addProperty("target", destination.ordinal());
        sendEvent(Event.SET_TITLE, obj);
    }

    private void sendEvent(Event event, JsonObject payload) {
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", event.getName());
        if (event.hasContext())
            eventJson.addProperty("context", -1); //TODO: Figure out what this is

        if (payload != null)
            eventJson.add("payload", payload);

        sendPayload(eventJson);
    }

    private void sendPayload(JsonObject object) {
        logger.trace("-- SENT: " + object.toString());
        ws.sendBinary(object.toString().getBytes());
    }
}
