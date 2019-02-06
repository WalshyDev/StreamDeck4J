package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

public class DidReceiveGlobalSettingsEvent extends Event {

    private final JsonObject settings;

    public DidReceiveGlobalSettingsEvent(
            @Nonnull JsonObject settings
    ) {
        super(null);
        this.settings = settings;
    }
}
