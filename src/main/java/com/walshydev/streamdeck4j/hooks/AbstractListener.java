package com.walshydev.streamdeck4j.hooks;

// TODO: Document

import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.ActionDisappearedEvent;
import com.walshydev.streamdeck4j.events.DeviceConnectedEvent;
import com.walshydev.streamdeck4j.events.Event;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.events.KeyUpEvent;
import com.walshydev.streamdeck4j.utils.SD4JLogger;

public abstract class AbstractListener implements EventListener {

    public void onDeviceConnected(DeviceConnectedEvent event) {
    }

    public void onActionAppeared(ActionAppearedEvent event) {
    }

    public void onActionDisappeared(ActionDisappearedEvent event) {
    }

    public void onKeyDown(KeyDownEvent event) {
    }

    public void onKeyUp(KeyUpEvent event) {
    }

    @Override
    public final void onEvent(Event event) {
        if (event instanceof DeviceConnectedEvent)
            onDeviceConnected((DeviceConnectedEvent) event);
        else if (event instanceof ActionAppearedEvent)
            onActionAppeared((ActionAppearedEvent) event);
        else if (event instanceof ActionDisappearedEvent)
            onActionDisappeared((ActionDisappearedEvent) event);
        else if (event instanceof KeyDownEvent)
            onKeyDown((KeyDownEvent) event);
        else if (event instanceof KeyUpEvent)
            onKeyUp((KeyUpEvent) event);

        else
            SD4JLogger.getLog(AbstractListener.class)
                .error("Unknown event was thrown! {}", event.getClass().getSimpleName());
    }
}
