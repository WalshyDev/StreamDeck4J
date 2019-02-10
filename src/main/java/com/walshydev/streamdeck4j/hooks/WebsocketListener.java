package com.walshydev.streamdeck4j.hooks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.walshydev.streamdeck4j.PluginImpl;
import com.walshydev.streamdeck4j.events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class WebsocketListener extends WebSocketAdapter {

    private final Logger logger = LoggerFactory.getLogger(WebsocketListener.class);
    private final JsonParser parser = new JsonParser();

    private final PluginImpl api;

    public WebsocketListener(PluginImpl api) {
        this.api = api;
    }

    public void handleCallbackError(WebSocket websocket, Throwable cause) {
        logger.error("Something bad happened on the WebSocket!", cause);
    }

    public void onStateChanged(WebSocket websocket, WebSocketState newState) {
        logger.trace("onStateChanged {}", newState.toString());
    }

    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        logger.trace("onConnected {}", headers.toString());
        if (!api.isRegistered()) {
            logger.trace("Registering - " + api.getPluginUUID());
            JsonObject registerJson = new JsonObject();
            registerJson.addProperty("event", "registerPlugin");
            registerJson.addProperty("uuid", api.getPluginUUID());
            api.sendPayload(registerJson);
            api.setRegistered(true);
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

        Event toSend = api.handleEvent(jsonObject);

        if (toSend == null)
            return;

        logger.trace("Firing event: {} to {} listeners.", toSend.getClass().getSimpleName(), api.getListeners().size());

        for (EventListener listener : api.getListeners())
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
}