import com.walshydev.streamdeck4j.PluginBuilder;
import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.KeyUpEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class ListenerExample extends AbstractListener {

    private static final Logger logger = LoggerFactory.getLogger(ListenerExample.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
            logger.error("Uncaught exception in {} - {}", t.getName(), e.getClass().getSimpleName(), e));
        Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
            logger.error("Uncaught exception in {} - {}", t.getName(), e.getClass().getSimpleName(), e));

        new PluginBuilder(args).registerListener(new ListenerExample())
            .buildAsync();
    }

    @Override
    public void onActionAppeared(ActionAppearedEvent event) {
        event.getPlugin().setTitle(event.getContext(), "Started!", Destination.HARDWARE_AND_SOFTWARE);
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        try {
            event.getPlugin().openURL(new URL("https://walshydev.com"));
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for some reason...", e);
        }
    }
}