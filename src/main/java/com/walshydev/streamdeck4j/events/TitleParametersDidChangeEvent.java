package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.info.Alignment;
import com.walshydev.streamdeck4j.info.Coordinates;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.awt.Font;


@Getter
public class TitleParametersDidChangeEvent extends Event {

    private final String action;
    private final String deviceId;
    private final JsonObject settings;
    private final Coordinates coordinates;
    private final int state;
    private final String title;
    private final Font titleFont;
    private final Color titleColor;
    private final Alignment alignment;

    public TitleParametersDidChangeEvent(
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId,
        @Nonnull JsonObject settings,
        @Nonnull Coordinates coordinates,
        int state,
        @Nonnull String title,
        @Nonnull Font titleFont,
        @Nonnull Color titleColor,
        @Nonnull Alignment alignment
    ) {
        super(context);
        this.action = action;
        this.deviceId = deviceId;
        this.settings = settings;
        this.coordinates = coordinates;
        this.state = state;
        this.title = title;
        this.titleFont = titleFont;
        this.titleColor = titleColor;
        this.alignment = alignment;
    }

}
