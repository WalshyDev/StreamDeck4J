package com.walshydev.streamdeck4j.hooks;

// TODO: Document

import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.Event;
import com.walshydev.streamdeck4j.events.DeviceConnectedEvent;
import org.slf4j.LoggerFactory;

public abstract class AbstractListener implements EventListener {

    public void onDeviceConnected(DeviceConnectedEvent event) {}
    public void onActionAppeared(ActionAppearedEvent event) {}

    @Override
    public final void onEvent(Event event) {
        if (event instanceof DeviceConnectedEvent)
            onDeviceConnected((DeviceConnectedEvent) event);
        else if (event instanceof ActionAppearedEvent)
            onActionAppeared((ActionAppearedEvent) event);

        else
            LoggerFactory.getLogger(AbstractListener.class)
                .error("Unknown event was thrown! {}", event.getClass().getSimpleName());
    }
}
