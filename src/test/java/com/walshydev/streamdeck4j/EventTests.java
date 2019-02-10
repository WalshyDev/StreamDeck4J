package com.walshydev.streamdeck4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.ActionDisappearedEvent;
import com.walshydev.streamdeck4j.events.ApplicationLaunchedEvent;
import com.walshydev.streamdeck4j.events.ApplicationTerminatedEvent;
import com.walshydev.streamdeck4j.events.DeviceConnectedEvent;
import com.walshydev.streamdeck4j.events.DeviceDisconnectedEvent;
import com.walshydev.streamdeck4j.events.Event;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.events.KeyUpEvent;
import com.walshydev.streamdeck4j.events.TitleParametersDidChangeEvent;
import com.walshydev.streamdeck4j.info.Alignment;
import com.walshydev.streamdeck4j.info.Coordinates;
import com.walshydev.streamdeck4j.info.Device;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventTests {

    private final String EXAMPLE_ACTION = "com.walshydev.example.action";
    private final String EXAMPLE_CONTEXT = "CONT3X_3X4MPL3";
    private final String EXAMPLE_DEVICE = "DEVICE_3X4MPL3";
    private final Gson gson = new Gson();

    private static PluginImpl plugin;

    @BeforeAll
    public static void setup() {
        plugin = new PluginImpl(new HashSet<>(), null);
    }

    @Test
    public void testKeyDownEvent() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.KEY_DOWN.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        Coordinates coordinates = new Coordinates(5, 3);

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);
        payload.add("coordinates", gson.toJsonTree(coordinates));
        payload.addProperty("state", 1);
        payload.addProperty("userDesiredState", 2);
        payload.addProperty("isInMultiAction", true);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(KeyDownEvent.class, event.getClass());

        KeyDownEvent keyDownEvent = (KeyDownEvent) event;
        assertEquals(EXAMPLE_ACTION, keyDownEvent.getAction());
        assertEquals(EXAMPLE_CONTEXT, keyDownEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, keyDownEvent.getDeviceId());
        assertEquals(coordinates.getColumn(), keyDownEvent.getCoordinates().getColumn());
        assertEquals(1, keyDownEvent.getState());
        assertEquals(2, keyDownEvent.getUserDesiredState());
        assertEquals("abc123", keyDownEvent.getSettings().get("test").getAsString());
        assertTrue(keyDownEvent.isMultiAction());
    }

    @Test
    public void testKeyUpEvent() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.KEY_UP.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        Coordinates coordinates = new Coordinates(5, 3);

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);
        payload.add("coordinates", gson.toJsonTree(coordinates));
        payload.addProperty("state", 1);
        payload.addProperty("userDesiredState", 2);
        payload.addProperty("isInMultiAction", true);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(KeyUpEvent.class, event.getClass());

        KeyUpEvent keyUpEvent = (KeyUpEvent) event;
        assertEquals(EXAMPLE_ACTION, keyUpEvent.getAction());
        assertEquals(EXAMPLE_CONTEXT, keyUpEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, keyUpEvent.getDeviceId());
        assertEquals(coordinates.getColumn(), keyUpEvent.getCoordinates().getColumn());
        assertEquals(1, keyUpEvent.getState());
        assertEquals(2, keyUpEvent.getUserDesiredState());
        assertEquals("abc123", keyUpEvent.getSettings().get("test").getAsString());
        assertTrue(keyUpEvent.isMultiAction());
    }

    @Test
    public void testWillAppearEvent() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.WILL_APPEAR.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        Coordinates coordinates = new Coordinates(5, 3);

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);
        payload.add("coordinates", gson.toJsonTree(coordinates));
        payload.addProperty("state", 1);
        payload.addProperty("isInMultiAction", true);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ActionAppearedEvent.class, event.getClass());

        ActionAppearedEvent actionAppearedEvent = (ActionAppearedEvent) event;
        assertEquals(EXAMPLE_ACTION, actionAppearedEvent.getAction());
        assertEquals(EXAMPLE_CONTEXT, actionAppearedEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, actionAppearedEvent.getDeviceId());
        assertEquals(coordinates.getColumn(), actionAppearedEvent.getCoordinates().getColumn());
        assertEquals("abc123", actionAppearedEvent.getSettings().get("test").getAsString());
        assertTrue(actionAppearedEvent.isMultiAction());
    }

    @Test
    public void testWillDisappearEvent() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.WILL_DISAPPEAR.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        Coordinates coordinates = new Coordinates(5, 3);

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);
        payload.add("coordinates", gson.toJsonTree(coordinates));
        payload.addProperty("state", 1);
        payload.addProperty("isInMultiAction", true);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ActionDisappearedEvent.class, event.getClass());

        ActionDisappearedEvent actionDisappearedEvent = (ActionDisappearedEvent) event;
        assertEquals(EXAMPLE_CONTEXT, actionDisappearedEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, actionDisappearedEvent.getDeviceId());
        assertEquals(coordinates.getColumn(), actionDisappearedEvent.getCoordinates().getColumn());
        assertEquals(EXAMPLE_ACTION, actionDisappearedEvent.getAction());
        assertEquals("abc123", actionDisappearedEvent.getSettings().get("test").getAsString());
        assertTrue(actionDisappearedEvent.isMultiAction());
    }

    @Test
    public void testTitleParametersDidChange() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.TITLE_PARAMETERS_DID_CHANGE.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        Coordinates coordinates = new Coordinates(5, 3);

        JsonObject titleParameters = new JsonObject();
        titleParameters.addProperty("fontFamily", "Comic Sans MS");
        titleParameters.addProperty("fontSize", 12);
        titleParameters.addProperty("fontStyle", "Bold Italic");
        titleParameters.addProperty("fontUnderline", true);
        titleParameters.addProperty("showTitle", true);
        titleParameters.addProperty("titleAlignment", "top");
        titleParameters.addProperty("titleColor", "#131313");

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);
        payload.add("coordinates", gson.toJsonTree(coordinates));
        payload.addProperty("state", 1);
        payload.addProperty("title", "Test Title");
        payload.add("titleParameters", titleParameters);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ActionDisappearedEvent.class, event.getClass());

        TitleParametersDidChangeEvent titleParametersDidChangeEvent = (TitleParametersDidChangeEvent) event;
        assertEquals(EXAMPLE_ACTION, titleParametersDidChangeEvent.getAction());
        assertEquals(EXAMPLE_CONTEXT, titleParametersDidChangeEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, titleParametersDidChangeEvent.getDeviceId());
        assertEquals("abc123", titleParametersDidChangeEvent.getSettings().get("test").getAsString());
        assertEquals(coordinates.getColumn(), titleParametersDidChangeEvent.getCoordinates().getColumn());
        assertEquals(1, titleParametersDidChangeEvent.getState());
        assertEquals("Test Title", titleParametersDidChangeEvent.getTitle());
        assertEquals("Comic Sans MS", titleParametersDidChangeEvent.getTitleFont().getFamily());
        assertEquals(12, titleParametersDidChangeEvent.getTitleFont().getSize());
        assertTrue(titleParametersDidChangeEvent.getTitleFont().isBold());
        assertTrue(titleParametersDidChangeEvent.getTitleFont().isItalic());
        assertEquals(
            TextAttribute.UNDERLINE_ON,
            titleParametersDidChangeEvent.getTitleFont().getAttributes().get(TextAttribute.UNDERLINE)
        );
        // TODO: Add `showTitle` check
//        assertTrue(titleParametersDidChangeEvent.get());
        assertEquals(Alignment.TOP, titleParametersDidChangeEvent.getAlignment());
        assertEquals(Color.decode("#131313"), titleParametersDidChangeEvent.getTitleColor());
    }

    @Test
    public void testDeviceDidConnect() {
        JsonObject obj = new JsonObject();
        obj.addProperty("event", SDEvent.DEVICE_DID_CONNECT.getName());
        obj.addProperty("device", EXAMPLE_DEVICE);
        obj.add("deviceInfo", gson.toJsonTree(new Device(new Device.Size(5, 3), (byte) 0)));

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(DeviceConnectedEvent.class, event.getClass());

        DeviceConnectedEvent deviceConnectedEvent = (DeviceConnectedEvent) event;
        assertEquals(EXAMPLE_DEVICE, deviceConnectedEvent.getDeviceId());
        assertEquals((byte) 0, deviceConnectedEvent.getDevice().getType());
        assertEquals(5, deviceConnectedEvent.getDevice().getSize().getColumns());
        assertEquals(3, deviceConnectedEvent.getDevice().getSize().getRows());
        assertEquals(15, deviceConnectedEvent.getDevice().getSize().getTotalKeys());
    }

    @Test
    public void testDeviceDidDisconnect() {
        JsonObject obj = new JsonObject();
        obj.addProperty("event", SDEvent.DEVICE_DID_DISCONNECT.getName());
        obj.addProperty("device", EXAMPLE_DEVICE);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(DeviceDisconnectedEvent.class, event.getClass());

        DeviceDisconnectedEvent deviceDisconnectedEvent = (DeviceDisconnectedEvent) event;
        assertEquals(EXAMPLE_DEVICE, deviceDisconnectedEvent.getDeviceId());
    }

    @Test
    public void testApplicationDidLaunch() {
        // Windows
        JsonObject obj = new JsonObject();
        obj.addProperty("event", SDEvent.APPLICATION_DID_LAUNCH.getName());

        JsonObject payload = new JsonObject();
        payload.addProperty("application", "StreamDeck.exe");

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ApplicationLaunchedEvent.class, event.getClass());

        ApplicationLaunchedEvent applicationLaunchedEvent = (ApplicationLaunchedEvent) event;
        assertEquals("StreamDeck.exe", applicationLaunchedEvent.getApplicationName());

        // Mac
        obj = new JsonObject();
        obj.addProperty("event", SDEvent.APPLICATION_DID_LAUNCH.getName());

        payload = new JsonObject();
        payload.addProperty("application", "com.apple.mail");

        obj.add("payload", payload);

        event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ApplicationLaunchedEvent.class, event.getClass());

        applicationLaunchedEvent = (ApplicationLaunchedEvent) event;
        assertEquals("com.apple.mail", applicationLaunchedEvent.getApplicationName());
    }

    @Test
    public void testApplicationDidTerminate() {
        // Windows
        JsonObject obj = new JsonObject();
        obj.addProperty("event", SDEvent.APPLICATION_DID_TERMINATE.getName());

        JsonObject payload = new JsonObject();
        payload.addProperty("application", "StreamDeck.exe");

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ApplicationTerminatedEvent.class, event.getClass());

        ApplicationTerminatedEvent applicationTerminatedEvent = (ApplicationTerminatedEvent) event;
        assertEquals("StreamDeck.exe", applicationTerminatedEvent.getApplicationName());

        // Mac
        obj = new JsonObject();
        obj.addProperty("event", SDEvent.APPLICATION_DID_TERMINATE.getName());

        payload = new JsonObject();
        payload.addProperty("application", "com.apple.mail");

        obj.add("payload", payload);

        event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(ApplicationTerminatedEvent.class, event.getClass());

        applicationTerminatedEvent = (ApplicationTerminatedEvent) event;
        assertEquals("com.apple.mail", applicationTerminatedEvent.getApplicationName());
    }
}
