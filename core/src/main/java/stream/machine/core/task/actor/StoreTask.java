package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import com.google.common.collect.ImmutableList;
import scala.concurrent.Future;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.SequenceConfiguration;
import stream.machine.core.configuration.StorageConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;
import stream.machine.core.task.Task;

import java.util.List;
import java.util.concurrent.Callable;

import static akka.dispatch.Futures.future;

/**
 * Created by Stephane on 19/02/2015.
 */
public class StoreTask extends TaskBase {

    private final EventStore store;
    private final int bulkSize;
    private final int bulkPeriod;


    public StoreTask(EventStore store, Configuration configuration, ActorSystem system) {
        super(configuration, system);
        StorageConfiguration storageConfiguration = new StorageConfiguration(configuration);
        this.store = store;
        this.bulkSize = storageConfiguration.getBulkSize();
        this.bulkPeriod = storageConfiguration.getBulkPeriodInMilliseconds();
    }

    @Override
    protected boolean canProcess() {
        return (store != null);
    }

    @Override
    protected Event doProcess(Event event) {
        try {
            return store.save(event);
        } catch (ApplicationException error) {
            event.put(getErrorField(),error.getMessage());
        }
        return event;
    }

    @Override
    public Future<List<Event>> processMultiple(List<Event> events) {
        if (this.store != null) {
            return future(new DoProcessMultiple(this.store, events), getSystem().dispatcher());
        } else {
            return Futures.successful((List<Event>) ImmutableList.copyOf(events));
        }
    }

    @Override
    public String getErrorField() {
        return "storeError";
    }


    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }

    private class DoProcessMultiple implements Callable<List<Event>> {
        private final List<Event> events;
        private final EventStore store;

        public DoProcessMultiple(EventStore store, List<Event> events) {
            this.store = store;
            this.events = events;
        }

        public List<Event> call() throws Exception {
            if (store != null) {
                return ImmutableList.copyOf(store.save(this.events));
            }
            return ImmutableList.copyOf(this.events);
        }
    }


}

