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
    LOG_MESSAGE("logMessage");

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
