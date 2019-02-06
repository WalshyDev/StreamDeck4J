package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.info.Coordinates;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter(onMethod = @__({@Nonnull}))
public class ActionAppearedEvent extends Event {

    @Nonnull
    private final String action;
    @Nonnull
    private final String deviceId;
    @Nonnull
    private final JsonObject settings;
    @Nonnull
    private final Coordinates coordinates;
    private final int state;
    private final boolean multiAction;

    public ActionAppearedEvent(
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId,
        @Nonnull JsonObject settings,
        @Nonnull Coordinates coordinates,
        int state,
        boolean multiAction
    ) {
        super(context);
        this.action = action;
        this.deviceId = deviceId;
        this.settings = settings;
        this.coordinates = coordinates;
        this.state = state;
        this.multiAction = multiAction;
    }
}
