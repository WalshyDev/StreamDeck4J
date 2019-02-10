package com.walshydev.streamdeck4j;

import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import com.walshydev.streamdeck4j.utils.SD4JLogger;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class SD4JMain {

    private static final Logger logger = SD4JLogger.getLog(SD4JMain.class);

    public static void main(String[] args) {
        logger.info("Starting main test app for StreamDeck4J");
        Plugin plugin = new PluginBuilder(args)
            .registerListener(new AbstractListener() {
                @Override
                public void onActionAppeared(ActionAppearedEvent event) {
                    event.getPlugin().setTitle(
                        event.getContext(),
                        "Testing123\n" + System.currentTimeMillis(),
                        Destination.HARDWARE_AND_SOFTWARE
                    );
                }

                @Override
                public void onKeyDown(KeyDownEvent event) {
                    try {
                        event.getPlugin().openURL(new URL("https://github.com/WalshyDev/StreamDeck4J"));
                        Thread.sleep(2000);
                        event.getPlugin().setImage(
                            event.getContext(),
                            ImageIO.read(getClass().getClassLoader().getResourceAsStream("blob.png")),
                            Destination.HARDWARE_AND_SOFTWARE
                        );
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            })
            .buildAsync();

        logger.info("Using StreamDeck version: {}", plugin.getApplication().getVersion());
    }
}
