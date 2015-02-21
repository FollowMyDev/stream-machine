package stream.machine.core.store.memory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stephane on 16/02/2015.
 */
public class MemoryEventStore extends ManageableBase implements EventStore {
    private final Map<String, MemoryStore<Event>> eventStore;

    public MemoryEventStore() {
        super("MemoryEventStore");
        eventStore = new ConcurrentHashMap<String, MemoryStore<Event>>(10);
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }

    @Override
    public List<Event> save(List<Event> events) {
        if (events != null && events.size() > 0) {
            for (Event event : events) {
               save(event);
            }
        }
        return ImmutableList.copyOf(events);
    }

    @Override
    public Event save(Event event) {
        if (event != null) {
            MemoryStore<Event> store = null;
            if (!eventStore.containsKey(event.getType())) {
                store = new MemoryStore<Event>();
                eventStore.put(event.getType(), store);
            } else {
                store = eventStore.get(event.getType());
            }
            try {
                if ( store != null) {
                    store.save(event.getKey().toString(), event);
                }
            } catch (ApplicationException error) {
                logger.error("Failed to  add event", event);
                event.put(Event.storeError, error.getMessage());
            }
        }
        return event;
    }

    @Override
    public List<Event> fetch(String eventType, DateTime startTime, DateTime stopTime) {
        final MemoryStore<Event> store = eventStore.get(eventType);
        if ( store != null)
        {
            final DateTime lowerBound = startTime;
            final DateTime upperBound = stopTime;
            Predicate<Event> matchDates = new Predicate<Event>() {
                public boolean apply(Event event) {
                    return event!= null && event.getTimestamp().isAfter(lowerBound) && event.getTimestamp().isBefore(upperBound) ;
                }
            };
            final List<Event> events = store.readAll();
            if (events != null && events.size() > 0)
            {
                ImmutableList.Builder<Event> builder= new ImmutableList.Builder<Event>();
                return builder.addAll(Collections2.filter(events,matchDates)).build();
            }
        }
        return null;
    }

    @Override
    public List<Event> update(List<Event> events) {
        if (events != null && events.size() > 0) {
            for (Event event : events) {
                update(event);
            }
        }
        return ImmutableList.copyOf(events);
    }

    @Override
    public Event update(Event event) {
        if (event != null) {
            MemoryStore<Event> store = null;
            if (!eventStore.containsKey(event.getType())) {
                store = new MemoryStore<Event>();
                eventStore.put(event.getType(), store);
            } else {
                store = eventStore.get(event.getType());
            }
            try {
                if ( store != null) {
                    store.update(event.getKey().toString(), event);
                }
            } catch (ApplicationException error) {
                logger.error("Failed to  add event", event);
                event.put(Event.storeError, error.getMessage());
            }
        }
        return event;
    }


}
