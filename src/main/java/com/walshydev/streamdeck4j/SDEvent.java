package com.walshydev.streamdeck4j;

public enum SDEvent {

    SET_TITLE("setTitle", true),
    SET_IMAGE("setImage", true),
    SHOW_ALERT("showAlert", true),
    SHOW_OK("showOk", true),
    GET_SETTINGS("getSettings", true),
    SET_SETTINGS("setSettings", true),
    GET_GLOBAL_SETTINGS("getGlobalSettings", true),
    SET_GLOBAL_SETTINGS("setGlobalSettings", true),
    SET_STATE("setState", true),
    SEND_TO_PROPERTY_INSPECTOR("sendToPropertyInspector", true),
    SWITCH_TO_PROFILE("switchToProfile", true),
    OPEN_URL("openUrl"),
    LOG_MESSAGE("logMessage"),

    KEY_DOWN("keyDown", true),
    KEY_UP("keyUp", true),
    WILL_APPEAR("willAppear", true),
    WILL_DISAPPEAR("willDisappear", true),
    TITLE_PARAMETERS_DID_CHANGE("titleParametersDidChange", true),
    DEVICE_DID_CONNECT("deviceDidConnect"),
    DEVICE_DID_DISCONNECT("deviceDidDisconnect"),
    APPLICATION_DID_LAUNCH("applicationDidLaunch"),
    APPLICATION_DID_TERMINATE("applicationDidTerminate"),
    SEND_TO_PLUGIN("sendToPlugin", true),
    DID_RECEIVE_SETTINGS("didReceiveSettings"),
    DID_RECEIVE_GLOBAL_SETTINGS("didReceiveGlobalSettings"),
    PROPERTY_INSPECTOR_DID_APPEAR("propertyInspectorDidAppear"),
    PROPERTY_INSPECTOR_DID_DISAPPEAR("propertyInspectorDidDisappear");

    private String name;
    private boolean hasContext;

    SDEvent(String name) {
        this.name = name;
        this.hasContext = false;
    }

    SDEvent(String name, boolean hasContext) {
        this.name = name;
        this.hasContext = hasContext;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasContext() {
        return this.hasContext;
    }
}
