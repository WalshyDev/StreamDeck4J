package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.Plugin;
import com.walshydev.streamdeck4j.info.Coordinates;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class DidReceiveSettingsEvent extends Event {

    private final String action;
    private final String deviceId;
    private final JsonObject settings;
    private final Coordinates coordinates;
    private final boolean multiAction;

    public DidReceiveSettingsEvent(
        @Nonnull Plugin plugin,
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId,
        @Nonnull JsonObject settings,
        @Nonnull Coordinates coordinates,
        boolean multiAction
    ) {
        super(plugin, context);
        this.action = action;
        this.deviceId = deviceId;
        this.settings = settings;
        this.coordinates = coordinates;
        this.multiAction = multiAction;
    }

}
