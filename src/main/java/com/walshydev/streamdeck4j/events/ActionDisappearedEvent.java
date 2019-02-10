package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.Plugin;
import com.walshydev.streamdeck4j.info.Coordinates;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter(onMethod_ = {@Nonnull})
public class ActionDisappearedEvent extends Event {

    private final String action;
    private final String deviceId;
    private final JsonObject settings;
    private final Coordinates coordinates;
    private final int state;
    private final boolean multiAction;

    public ActionDisappearedEvent(
        @Nonnull Plugin plugin,
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId,
        @Nonnull JsonObject settings,
        @Nonnull Coordinates coordinates,
        int state,
        boolean multiAction
    ) {
        super(plugin, context);
        this.action = action;
        this.deviceId = deviceId;
        this.settings = settings;
        this.coordinates = coordinates;
        this.state = state;
        this.multiAction = multiAction;
    }
}
