package stream.machine.core.store.memory;

import org.joda.time.DateTime;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.store.EventStore;

import java.util.List;

/**
 * Created by Stephane on 16/02/2015.
 */
public class MemoryEventStore extends ManageableBase implements EventStore {
    protected MemoryEventStore(String name) {
        super(name);
    }

    @Override
    public List<Event> save(List<Event> events) {
        return null;
    }

    @Override
    public Event save(Event event) {
        return null;
    }

    @Override
    public List<Event> fetch(String eventType, DateTime startTime, DateTime stopTime) {
        return null;
    }

    @Override
    public List<Event> update(List<Event> events) {
        return null;
    }

    @Override
    public Event update(Event event) {
        return null;
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }
}
