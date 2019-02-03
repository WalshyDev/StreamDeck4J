package com.walshydev.streamdeck4j;

import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class SD4JMain {

    private static final Logger logger = LoggerFactory.getLogger(SD4JMain.class);

    public static void main(String[] args) {
        logger.info("Starting main test app for StreamDeck4J");
        StreamDeck4J streamDeck4J = new StreamDeck4J();

        streamDeck4J.addListener(new AbstractListener() {
            @Override
            public void onActionAppeared(ActionAppearedEvent event) {
                streamDeck4J.setTitle(
                    event.getContext(),
                    "Testing123\n" + System.currentTimeMillis(),
                    Destination.HARDWARE_AND_SOFTWARE
                );
            }

            @Override
            public void onKeyDown(KeyDownEvent event) {
                try {
                    streamDeck4J.openURL(new URL("https://github.com/WalshyDev/StreamDeck4J"));
                    Thread.sleep(2000);
                    streamDeck4J.setImage(
                        event.getContext(),
                        ImageIO.read(getClass().getClassLoader().getResourceAsStream("blob.png")),
                        Destination.HARDWARE_AND_SOFTWARE
                    );
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        logger.info("Connecting!");
        streamDeck4J.connect(args);
    }
}
