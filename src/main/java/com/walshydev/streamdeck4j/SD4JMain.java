package com.walshydev.streamdeck4j;

import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SD4JMain {

    private static final Logger logger = LoggerFactory.getLogger(SD4JMain.class);

    public static void main(String[] args) {
        logger.info("Starting main test app for StreamDeck4J");
        StreamDeck4J streamDeck4J = new StreamDeck4J();

        streamDeck4J.addListener(new AbstractListener() {
            @Override
            public void onActionAppeared(ActionAppearedEvent event) {
                logger.info("Received event!!! Whoooo! Sending a title!");
                streamDeck4J.setTitle(
                    "Testing123\n" + System.currentTimeMillis(),
                    Destination.GARDWARE_AND_SOFTWARE,
                    event.getContext()
                );
            }
        });

        logger.info("Connecting!");
        streamDeck4J.connect(args);
    }
}
