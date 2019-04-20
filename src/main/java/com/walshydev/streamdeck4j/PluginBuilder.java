package com.walshydev.streamdeck4j;

import com.neovisionaries.ws.client.WebSocketFactory;
import com.walshydev.streamdeck4j.hooks.EventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PluginBuilder {

    protected final String[] args;
    protected final Set<EventListener> listeners;

    protected WebSocketFactory wsFactory;

    public PluginBuilder(String[] args) {
        this.args = args;

        this.listeners = new HashSet<>();
    }

    public PluginBuilder registerListener(EventListener listener) {
        this.listeners.add(listener);
        return this;
    }

    public PluginBuilder registerListener(EventListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
        return this;
    }

    public PluginBuilder setWebsocketFactory(WebSocketFactory wsFactory) {
        this.wsFactory = wsFactory;
        return this;
    }

    public Plugin build() {
        PluginImpl plugin = new PluginImpl(listeners, wsFactory);

        plugin.connect(args, false);
        return plugin;
    }

    public Plugin buildAsync() {
        PluginImpl plugin = new PluginImpl(listeners, wsFactory);

        plugin.connect(args, true);
        return plugin;
    }
}
