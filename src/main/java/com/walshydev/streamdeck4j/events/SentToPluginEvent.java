package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class SentToPluginEvent extends Event {

    private final String action;
    private final JsonObject payload;

    public SentToPluginEvent(
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull JsonObject payload
    ) {
        super(context);
        this.action = action;
        this.payload = payload;
    }

}
