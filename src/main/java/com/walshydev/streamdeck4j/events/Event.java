package com.walshydev.streamdeck4j.events;

import com.walshydev.streamdeck4j.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public abstract class Event {

    private final String context;
    private final Plugin plugin;

    public Event(@Nonnull Plugin plugin, @Nullable String context) {
        this.plugin = plugin;
        this.context = context;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getContext() {
        return this.context;
    }
}
