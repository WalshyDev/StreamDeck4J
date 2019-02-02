package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.info.Coordinates;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class KeyDownEvent extends Event {

    private final String action;
    private final String deviceId;
    private final JsonObject settings;
    private final Coordinates coordinates;
    private final int state;
    private final int userDesiredState;
    private final boolean multiAction;

    public KeyDownEvent(
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId,
        @Nonnull JsonObject settings,
        @Nonnull Coordinates coordinates,
        int state,
        int userDesiredState,
        boolean multiAction
    ) {
        super(context);
        this.action = action;
        this.deviceId = deviceId;
        this.settings = settings;
        this.coordinates = coordinates;
        this.state = state;
        this.userDesiredState = userDesiredState;
        this.multiAction = multiAction;
    }
}
