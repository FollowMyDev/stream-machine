package stream.machine.core.task;

import scala.concurrent.Future;
import stream.machine.core.manager.Manageable;
import stream.machine.core.model.Event;

/**
 * Created by Stephane on 31/01/2015.
 */
public interface Task extends Manageable {
    Future<Event> process(Event event);
}