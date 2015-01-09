package stream.machine.core.task.store;

import stream.machine.core.model.Event;
import stream.machine.core.model.Query;

import java.util.List;

/**
 * Created by Stephane on 06/12/2014.
 */
public interface EventStore extends Store {
    List<Event>  save(final List<Event> events);
    Event save(final Event event);
    List<Event> fetch(final Query query);
    List<Event> update(final List<Event> events);
    Event update(final Event event);

}
