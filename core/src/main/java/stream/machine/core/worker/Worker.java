package stream.machine.core.worker;

import scala.concurrent.Future;
import stream.machine.core.manager.Manageable;
import stream.machine.core.model.Event;

/**
 * Created by Stephane on 31/01/2015.
 */
public interface Worker extends Manageable {
    Future<Event> transform(Event event);
}
