import com.walshydev.streamdeck4j.SD4JMain;
import com.walshydev.streamdeck4j.StreamDeck4J;
import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class Example {

    private static final Logger logger = LoggerFactory.getLogger(SD4JMain.class);

    public static void main(String[] args) {
        StreamDeck4J streamDeck4J = new StreamDeck4J();

        streamDeck4J.addListener(new AbstractListener() {
            @Override
            public void onActionAppeared(ActionAppearedEvent event) {
                streamDeck4J.setTitle(
                    event.getContext(),
                    "Made With\nStreamDeck4J!",
                    Destination.HARDWARE_AND_SOFTWARE
                );
            }

            @Override
            public void onKeyDown(KeyDownEvent event) {
                try {
                    streamDeck4J.openURL(new URL("https://github.com/WalshyDev/StreamDeck4J"));
                    streamDeck4J.showOk(event.getContext());
                } catch (MalformedURLException e) {
                    logger.error("Invalid URL", e);
                }
            }
        });

        streamDeck4J.connect(args);
    }
}
