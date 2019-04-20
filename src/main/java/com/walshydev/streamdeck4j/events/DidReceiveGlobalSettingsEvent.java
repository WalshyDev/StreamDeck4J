package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.Plugin;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class DidReceiveGlobalSettingsEvent extends Event {

    private final JsonObject settings;

    public DidReceiveGlobalSettingsEvent(
        @Nonnull Plugin plugin,
        @Nonnull JsonObject settings
    ) {
        super(plugin, null);
        this.settings = settings;
    }
}
