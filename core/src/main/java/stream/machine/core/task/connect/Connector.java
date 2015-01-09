package stream.machine.core.task.connect;

import stream.machine.core.model.Event;
import stream.machine.core.streaming.Stream;

import java.util.UUID;

/**
 * Created by Stephane on 06/12/2014.
 */
public interface Connector  {
    boolean connect(Stream<Event> eventStream);
    boolean disconnect(UUID streamId);
}
