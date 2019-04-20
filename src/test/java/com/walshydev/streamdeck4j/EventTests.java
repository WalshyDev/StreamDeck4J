package com.walshydev.streamdeck4j;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.events.*;
import com.walshydev.streamdeck4j.info.Alignment;
import com.walshydev.streamdeck4j.info.Coordinates;
import com.walshydev.streamdeck4j.info.Device;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTests {

    private final String EXAMPLE_ACTION = "com.walshydev.example.action";
    private final String EXAMPLE_CONTEXT = "CONT3X_3X4MPL3";
    private final String EXAMPLE_DEVICE = "DEVICE_3X4MPL3";
    private final Gson gson = new Gson();

    private static PluginImpl plugin;

    @BeforeAll
    static void setup() {
        plugin = new PluginImpl(new HashSet<>(), null);
    }

    @Test
    void testKeyDownEvent() {
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
    void testKeyUpEvent() {
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
    void testWillAppearEvent() {
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
    void testWillDisappearEvent() {
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
    void testTitleParametersDidChange() {
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
        assertEquals(TitleParametersDidChangeEvent.class, event.getClass());

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
        assertTrue(titleParametersDidChangeEvent.isShowingTitle());
        assertEquals(Alignment.TOP, titleParametersDidChangeEvent.getAlignment());
        assertEquals(Color.decode("#131313"), titleParametersDidChangeEvent.getTitleColor());
    }

    @Test
    void testDeviceDidConnect() {
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
    void testDeviceDidDisconnect() {
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
    void testApplicationDidLaunch() {
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
    void testApplicationDidTerminate() {
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

    @Test
    void testSendToPlugin() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.SEND_TO_PLUGIN.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);

        JsonObject payload = new JsonObject();
        payload.addProperty("SomePayloadData", "abc123");
        payload.addProperty("SomeOtherPayloadData", 1.245);

        JsonArray arr = new JsonArray();
        arr.add('a');
        arr.add("1");
        arr.add(false);
        payload.add("SomeArrayData", arr);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(SentToPluginEvent.class, event.getClass());

        SentToPluginEvent sentToPlugin = (SentToPluginEvent) event;
        assertEquals(EXAMPLE_ACTION, sentToPlugin.getAction());
        assertEquals(EXAMPLE_CONTEXT, sentToPlugin.getContext());

        JsonObject returnedPayload = sentToPlugin.getPayload();
        assertNotNull(returnedPayload);
        assertEquals("abc123", returnedPayload.get("SomePayloadData").getAsString());
        assertEquals(1.245, returnedPayload.get("SomeOtherPayloadData").getAsDouble());
        assertEquals(arr, returnedPayload.get("SomeArrayData").getAsJsonArray());
    }

    @Test
    void testDidReceiveSettings() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.DID_RECEIVE_SETTINGS.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        Coordinates coordinates = new Coordinates(5, 3);

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);
        payload.add("coordinates", gson.toJsonTree(coordinates));
        payload.addProperty("isInMultiAction", false);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(DidReceiveSettingsEvent.class, event.getClass());

        DidReceiveSettingsEvent didReceiveSettings = (DidReceiveSettingsEvent) event;
        assertEquals(EXAMPLE_ACTION, didReceiveSettings.getAction());
        assertEquals(EXAMPLE_CONTEXT, didReceiveSettings.getContext());
        assertEquals(EXAMPLE_DEVICE, didReceiveSettings.getDeviceId());
        assertEquals("abc123", didReceiveSettings.getSettings().get("test").getAsString());
        assertEquals(coordinates.getColumn(), didReceiveSettings.getCoordinates().getColumn());
        assertFalse(didReceiveSettings.isMultiAction());
    }

    @Test
    void testDidReceiveGlobalSettings() {
        JsonObject obj = new JsonObject();
        obj.addProperty("event", SDEvent.DID_RECEIVE_GLOBAL_SETTINGS.getName());

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(DidReceiveGlobalSettingsEvent.class, event.getClass());

        DidReceiveGlobalSettingsEvent didReceiveSettings = (DidReceiveGlobalSettingsEvent) event;
        assertEquals("abc123", didReceiveSettings.getSettings().get("test").getAsString());
    }

    @Test
    void testPropertyInspectorDidAppear() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.PROPERTY_INSPECTOR_DID_APPEAR.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(PropertyInspectorDidAppearEvent.class, event.getClass());

        PropertyInspectorDidAppearEvent propertyInspectorDidAppearEvent = (PropertyInspectorDidAppearEvent) event;
        assertEquals(EXAMPLE_ACTION, propertyInspectorDidAppearEvent.getAction());
        assertEquals(EXAMPLE_CONTEXT, propertyInspectorDidAppearEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, propertyInspectorDidAppearEvent.getDeviceId());
    }

    @Test
    void testPropertyInspectorDidDisappear() {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", EXAMPLE_ACTION);
        obj.addProperty("event", SDEvent.PROPERTY_INSPECTOR_DID_DISAPPEAR.getName());
        obj.addProperty("context", EXAMPLE_CONTEXT);
        obj.addProperty("device", EXAMPLE_DEVICE);

        JsonObject settings = new JsonObject();
        settings.addProperty("test", "abc123");

        JsonObject payload = new JsonObject();
        payload.add("settings", settings);

        obj.add("payload", payload);

        Event event = plugin.handleEvent(obj);

        assertNotNull(event);
        assertEquals(PropertyInspectorDidDisappearEvent.class, event.getClass());

        PropertyInspectorDidDisappearEvent propertyInspectorDidDisappearEvent =
            (PropertyInspectorDidDisappearEvent)event;
        assertEquals(EXAMPLE_ACTION, propertyInspectorDidDisappearEvent.getAction());
        assertEquals(EXAMPLE_CONTEXT, propertyInspectorDidDisappearEvent.getContext());
        assertEquals(EXAMPLE_DEVICE, propertyInspectorDidDisappearEvent.getDeviceId());
    }
}
