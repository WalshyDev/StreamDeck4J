package com.walshydev.streamdeck4j;

public enum Event {

    SET_TITLE("setTitle", true),
    SET_IMAGE("setImage", true),
    SHOW_ALERT("showAlert", true),
    SHOW_OK("showOk", true),
    SET_SETTINGS("setSettings", true),
    SET_STATE("setState", true),
    SEND_TO_PROPERY_INSPECTOR("sendToPropertyInspector", true),
    SWITCH_TO_PROFILE("switchToProfile", true),
    OPEN_URL("openUrl"),

    KEY_DOWN("keyDown", true, true),
    KEY_UP("keyUp", true, true),
    WILL_APPEAR("willAppear", true, true),
    WILL_DISAPPEAR("willDisappear", true, true),
    TITLE_PARAMETERS_DID_CHANGE("titleParametersDidChange", true, true),
    DEVICE_DID_CONNECT("deviceDidConnect", true),
    DEVICE_DID_DISCONNECT("deviceDidDisconnect", true),
    APPLICATION_DID_LAUNCH("applicationDidLaunch", true),
    APPLICATION_DID_TERMINATE("applicationDidTerminate", true);

    private String name;
    private boolean hasContext;
    private boolean receiveEvent;

    Event(String name) {
        this.name = name;
        this.hasContext = false;
        this.receiveEvent = false;
    }

    Event(String name, boolean hasContext) {
        this.name = name;
        this.hasContext = hasContext;
    }

    Event(String name, boolean hasContext, boolean receiveEvent) {
        this.name = name;
        this.hasContext = hasContext;
        this.receiveEvent = receiveEvent;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasContext() {
        return this.hasContext;
    }

    public boolean isReceiveEvent() {
        return this.receiveEvent;
    }
}
