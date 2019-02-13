package com.walshydev.streamdeck4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.walshydev.streamdeck4j.events.*;
import com.walshydev.streamdeck4j.hooks.EventListener;
import com.walshydev.streamdeck4j.hooks.WebsocketListener;
import com.walshydev.streamdeck4j.info.Alignment;
import com.walshydev.streamdeck4j.info.Application;
import com.walshydev.streamdeck4j.info.Coordinates;
import com.walshydev.streamdeck4j.info.Destination;
import com.walshydev.streamdeck4j.info.Device;
import com.walshydev.streamdeck4j.utils.SD4JLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue", "MagicConstant"})
public final class PluginImpl implements Plugin {

    private static final Logger logger = SD4JLogger.getLog(Plugin.class);

    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();
    private final Set<EventListener> listeners;
    private final WebSocketFactory wsFactory;

    private CommandLine cmd;
    private WebSocket ws;
    private UUID pluginUUID;
    private Application application;
    private Set<Device> devices;
    private int devicePixelRatio;
    private boolean registered;

    public PluginImpl(Set<EventListener> listeners, WebSocketFactory factory) {
        this.listeners = listeners;
        this.wsFactory = factory == null ? new WebSocketFactory() : factory;
    }

    /**
     * Connects to the websocket for Stream Deck
     *
     * @param args Program arguments, such as Websocket port, plugin UUID, etc.
     */
    public void connect(String[] args, boolean async) {
        logger.trace("Hello, World!");
        logger.trace("Parsing arguments - " + Arrays.toString(args));
        parseArguments(args);
        logger.trace("Parsed arguments - " + Arrays.toString(cmd.getOptions()));

        Thread.setDefaultUncaughtExceptionHandler(((t, e) ->
            logger.error("Uncaught exception in {} - {}", t.getName(), e.getClass().getSimpleName(), e))
        );
        Thread.currentThread().setUncaughtExceptionHandler(((t, e) ->
            logger.error("Uncaught exception in {} - {}", t.getName(), e.getClass().getSimpleName(), e))
        );

        logger.debug("Connecting to websocket");
        connectToWebsocket(async);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ws.isOpen())
                ws.sendClose();
        }));
    }

    //////////////////////////
    // INTERNALS
    //////////////////////////
    public void parseArguments(String[] args) {
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

    public void connectToWebsocket(boolean async) {
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
        if (infoObj.has("devicePixelRatio"))
            this.devicePixelRatio = infoObj.get("devicePixelRatio").getAsInt();
        else
            logger.warn("Using SDK v1 - It is recommended to use SDK version 2!");

        try {
            logger.debug("Creating websocket at 'ws://localhost:{}'", cmd.getOptionValue("port"));
            ws = wsFactory.createSocket("ws://localhost:" + cmd.getOptionValue("port"));
        } catch (IOException e) {
            logger.error("Failed to create websocket!", e);
        }

        ws.addListener(new WebsocketListener(this));

        if (async)
            ws.connectAsynchronously();
        else {
            try {
                ws.connect();
            } catch (WebSocketException e) {
                logger.error("Failed to connect to WebSocket!", e);
                System.exit(1);
            }
        }
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public void sendEvent(@Nonnull SDEvent event, @Nonnull JsonObject payload) {
        logger.trace("sendEvent(SDEvent, payload)");
        if (event.hasContext())
            throw new IllegalArgumentException("No context passed but " + event.getName() + " needs one!");

        sendEvent(event, payload, null);
    }

    public void sendEvent(@Nonnull SDEvent event, @Nullable JsonObject payload, @Nullable String context) {
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

    public void sendPayload(@Nonnull JsonObject object) {
        logger.trace("-- SENT: " + object.toString());
        ws.sendBinary(object.toString().getBytes());
    }

    @SuppressWarnings("unchecked")
    public Font getFont(JsonObject titleParameters) {
        int fontSize = titleParameters.get("fontSize").getAsInt();
        if (fontSize > 18 || fontSize < 6)
            throw new IllegalArgumentException("Received font size value outside of the range 6-18... is this ever meant to happen?");
        Font f = new Font(
            titleParameters.get("fontFamily").getAsString(),
            getFontStyle(titleParameters.get("fontStyle").getAsString()),
            fontSize
        );

        if (titleParameters.get("fontUnderline").getAsBoolean()) {
            Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) f.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            f = f.deriveFont(attributes);
        }
        return f;
    }

    public int getFontStyle(String fontStyle) {
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

    public Set<EventListener> getListeners() {
        return listeners;
    }

    public Event handleEvent(@Nonnull JsonObject jsonObject) {
        final String event = jsonObject.get("event").getAsString();

        @Nullable JsonObject payload = null;
        if (jsonObject.has("payload"))
            payload = jsonObject.get("payload").getAsJsonObject();

        switch (event) {
            case "keyDown":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }

                return new KeyDownEvent(
                    this,
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
            case "keyUp":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }

                return new KeyUpEvent(
                    this,
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
            case "willAppear":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }

                return new ActionAppearedEvent(
                    this,
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("action").getAsString(),
                    jsonObject.get("device").getAsString(),
                    // Payload data
                    payload.get("settings").getAsJsonObject(),
                    gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                    payload.has("state") ? payload.get("state").getAsInt() : 0,
                    payload.get("isInMultiAction").getAsBoolean()
                );
            case "willDisappear":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }

                return new ActionDisappearedEvent(
                    this,
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("action").getAsString(),
                    jsonObject.get("device").getAsString(),
                    // Payload data
                    payload.get("settings").getAsJsonObject(),
                    gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                    payload.has("state") ? payload.get("state").getAsInt() : 0,
                    payload.get("isInMultiAction").getAsBoolean()
                );
            case "titleParametersDidChange":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }
                JsonObject titleParameters = payload.get("titleParameters").getAsJsonObject();
                return new TitleParametersDidChangeEvent(
                    this,
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("action").getAsString(),
                    jsonObject.get("device").getAsString(),

                    // Payload data
                    payload.get("settings").getAsJsonObject(),
                    gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                    payload.get("state").getAsInt(),
                    payload.get("title").getAsString(),

                    // Title Parameters
                    titleParameters.get("showTitle").getAsBoolean(),
                    getFont(titleParameters),
                    Color.decode(titleParameters.get("titleColor").getAsString()),
                    Alignment.valueOf(titleParameters.get("titleAlignment").getAsString().toUpperCase())
                );
            case "deviceDidConnect":
                return new DeviceConnectedEvent(
                    this,
                    jsonObject.get("device").getAsString(),
                    gson.fromJson(jsonObject.get("deviceInfo").getAsJsonObject(), Device.class)
                );
            case "deviceDidDisconnect":
                return new DeviceDisconnectedEvent(this, jsonObject.get("device").getAsString());
            case "applicationDidLaunch":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }
                return new ApplicationLaunchedEvent(this, payload.get("application").getAsString());
            case "applicationDidTerminate":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }
                return new ApplicationTerminatedEvent(this, payload.get("application").getAsString());
            case "sendToPlugin":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }
                return new SentToPluginEvent(
                    this,
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("action").getAsString(),
                    payload
                );
            case "didReceiveSettings":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }

                return new DidReceiveSettingsEvent(
                    this,
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("action").getAsString(),
                    jsonObject.get("device").getAsString(),
                    // Payload data
                    payload.get("settings").getAsJsonObject(),
                    gson.fromJson(payload.get("coordinates").getAsJsonObject(), Coordinates.class),
                    payload.get("isInMultiAction").getAsBoolean()
                );
            case "didReceiveGlobalSettings":
                if (payload == null) {
                    logger.error("Invalid JSON for {}", event);
                    break;
                }

                return new DidReceiveGlobalSettingsEvent(
                    this,
                    payload.get("settings").getAsJsonObject()
                );
            case "propertyInspectorDidAppear":
                return new PropertyInspectorDidAppearEvent(
                    this,
                    jsonObject.get("action").getAsString(),
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("device").getAsString()
                );
            case "propertyInspectorDidDisappear":
                return new PropertyInspectorDidDisappearEvent(
                    this,
                    jsonObject.get("action").getAsString(),
                    jsonObject.get("context").getAsString(),
                    jsonObject.get("device").getAsString()
                );
            default:
                logger.warn(
                    "Received unknown event! '{}' - Ignoring for now!",
                    jsonObject.get("event").getAsString()
                );
        }
        return null;
    }

    /////////////////////////
    // Public methods
    /////////////////////////

    @Override
    public void addListener(@Nonnull EventListener eventListener) {
        this.listeners.add(eventListener);
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public Set<Device> getDevices() {
        return this.devices;
    }

    @Override
    public int getDevicePixelRatio() {
        return this.devicePixelRatio;
    }

    @Override
    public String getPluginUUID() {
        return this.pluginUUID.toString().toUpperCase();
    }

    @Override
    public void openURL(@Nonnull URL url) {
        logger.trace("openURL(url)");
        JsonObject payload = new JsonObject();
        payload.addProperty("url", url.toString());

        sendEvent(SDEvent.OPEN_URL, payload);
    }

    @Override
    public void setTitle(@Nonnull String context, @Nonnull String title, @Nonnull Destination destination) {
        logger.trace("setTitle(context, title, destination)");
        JsonObject obj = new JsonObject();
        obj.addProperty("title", title);
        obj.addProperty("target", destination.ordinal());
        sendEvent(SDEvent.SET_TITLE, obj, context);
    }

    @Override
    public void setImage(@Nonnull String context, BufferedImage image, Destination destination) {
        logger.trace("setImage(context, image, destination)");
        setImage(context, image, "png", destination);
    }

    @Override
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

    @Override
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

    @Override
    public void showAlert(@Nonnull String context) {
        logger.trace("showAlert(context)");
        sendEvent(SDEvent.SHOW_ALERT, null, context);
    }

    @Override
    public void showOk(@Nonnull String context) {
        logger.trace("showOk(context)");
        sendEvent(SDEvent.SHOW_OK, null, context);
    }

    @Override
    public void getSettings(@Nonnull String context) {
        logger.trace("getSettings(context)");
        sendEvent(SDEvent.GET_SETTINGS, null, context);
        // TODO: Implement returning the JsonObject from this event. Walshy, I'm looking at you :eyes:
        // Docs: https://edge.elgato.com/egc/sd_content/releasenotes/4.1b1.html
    }

    @Override
    public void setSettings(@Nonnull String context, @Nonnull JsonObject dataToSave) {
        logger.trace("setSettings(context, dataToSave)");
        sendEvent(SDEvent.SET_SETTINGS, dataToSave, context);
    }

    @Override
    public void getGlobalSettings() {
        logger.trace("getGlobalSettings(context)");
        sendEvent(SDEvent.GET_GLOBAL_SETTINGS, null, getPluginUUID());
        // TODO: Implement return value.
    }

    @Override
    public void setGlobalSettings(@Nonnull JsonObject dataToSave) {
        logger.trace("setGlobalSettings(context, dataToSave)");
        sendEvent(SDEvent.SET_GLOBAL_SETTINGS, dataToSave, getPluginUUID());
    }

    @Override
    public void setState(@Nonnull String context, int state) {
        logger.trace("setState(context, state)");
        JsonObject payload = new JsonObject();
        payload.addProperty("state", state);
        sendEvent(SDEvent.SET_STATE, payload, context);
    }

    @Override
    public void sendToPropertyInspector(@Nonnull String context, @Nonnull String action, @Nonnull JsonObject payload) {
        logger.trace("sendToPropertyInspector(context, action, payload)");
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", SDEvent.SEND_TO_PROPERTY_INSPECTOR.getName());
        eventJson.addProperty("context", context);
        eventJson.addProperty("action", action);
        eventJson.add("payload", payload);

        sendPayload(eventJson);
    }

    @Override
    public void switchToProfile(@Nonnull String deviceId, @Nonnull String profile) {
        logger.trace("switchToProfile(context, deviceId, payload)");
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", SDEvent.SWITCH_TO_PROFILE.getName());
        eventJson.addProperty("context", getPluginUUID());
        eventJson.addProperty("device", deviceId);

        JsonObject payload = new JsonObject();
        payload.addProperty("profile", profile);

        eventJson.add("payload", payload);

        sendPayload(eventJson);
    }

    @Override
    public void logMessage(@Nonnull String message) {
        logger.trace("logMessage(message)");
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("event", SDEvent.LOG_MESSAGE.getName());

        JsonObject payload = new JsonObject();
        payload.addProperty("message", message);

        eventJson.add("payload", payload);

        sendPayload(eventJson);
    }
}
