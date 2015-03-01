package stream.machine.core.monitor;

import java.util.UUID;

/**
 * Created by Stephane on 28/02/2015.
 */
public interface MonitorConsumer {
    String getName();
    void onMessage(Message message);
}
