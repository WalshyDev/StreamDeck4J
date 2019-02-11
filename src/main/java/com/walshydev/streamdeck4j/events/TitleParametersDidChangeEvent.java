package com.walshydev.streamdeck4j.events;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.Plugin;
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
    private final boolean showingTitle;
    private final Font titleFont;
    private final Color titleColor;
    private final Alignment alignment;

    public TitleParametersDidChangeEvent(
        @Nonnull Plugin plugin,
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId,
        @Nonnull JsonObject settings,
        @Nonnull Coordinates coordinates,
        int state,
        @Nonnull String title,
        boolean showingTitle,
        @Nonnull Font titleFont,
        @Nonnull Color titleColor,
        @Nonnull Alignment alignment
    ) {
        super(plugin, context);
        this.action = action;
        this.deviceId = deviceId;
        this.settings = settings;
        this.coordinates = coordinates;
        this.state = state;
        this.title = title;
        this.showingTitle = showingTitle;
        this.titleFont = titleFont;
        this.titleColor = titleColor;
        this.alignment = alignment;
    }

}
