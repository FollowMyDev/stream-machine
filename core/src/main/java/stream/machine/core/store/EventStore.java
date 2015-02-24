package stream.machine.core.store;

import org.joda.time.DateTime;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.util.List;

/**
 * Created by Stephane on 06/12/2014.
 */
public interface EventStore extends Store {
    List<Event> save(final List<Event> events) throws ApplicationException;

    Event save(final Event event) throws ApplicationException;

    List<Event> fetch(String eventType, DateTime startTime, DateTime stopTime) throws ApplicationException;

    List<Event> update(final List<Event> events) throws ApplicationException;

    Event update(final Event event) throws ApplicationException;

}
