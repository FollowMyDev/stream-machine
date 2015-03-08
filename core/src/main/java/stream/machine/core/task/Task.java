package stream.machine.core.task;


import com.google.common.util.concurrent.ListenableFuture;
import stream.machine.core.manager.Manageable;
import stream.machine.core.model.Event;

import java.util.List;

/**
 * Created by Stephane on 31/01/2015.
 */
public interface Task extends Manageable {
    ListenableFuture<Event> process(Event event);

    ListenableFuture<List<Event>> processMultiple(List<Event> events);

    String getErrorField();
}
