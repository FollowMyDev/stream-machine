package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import scala.concurrent.Future;
import stream.machine.core.configuration.store.EventStorageConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;
import stream.machine.core.task.Task;

import java.util.concurrent.Callable;

import static akka.dispatch.Futures.future;

/**
 * Created by Stephane on 19/02/2015.
 */
public class StoreTask extends ManageableBase implements Task {

    private final EventStore store;
    private final ActorSystem system;
    private final int bulkSize;
    private final int bulkPeriod;


    public StoreTask(EventStore store, EventStorageConfiguration configuration, ActorSystem system) {
        super(configuration.getName());
        this.system = system;
        this.store = store;
        this.bulkSize = configuration.getBulkSize();
        this.bulkPeriod = configuration.getBulkPeriodInMilliseconds();
    }

    @Override
    public Future<Event> process(Event event) {
        if (this.store != null) {
            return future(new DoProcess(this.store, event), system.dispatcher());
        } else {
            return Futures.successful(event);
        }
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }

    private class DoProcess implements Callable<Event> {

        private final Event event;
        private final EventStore store;

        public DoProcess(EventStore store, Event event) {
            this.store = store;
            this.event = event;
        }

        public Event call() throws Exception {
            if (store != null) {
                return store.save(event);
            }
            return event;
        }
    }

}
