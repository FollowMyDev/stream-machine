package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import com.google.common.collect.ImmutableList;
import scala.concurrent.Future;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;

import java.util.List;
import java.util.concurrent.Callable;

import static akka.dispatch.Futures.future;

/**
 * Created by Stephane on 27/02/2015.
 */
public abstract class TaskBase extends ManageableBase implements Task {
    private final ActorSystem system;

    protected TaskBase(Configuration configuration, ActorSystem system) {
        super(configuration.getName());
        this.system = system;
    }

    protected ActorSystem getSystem() {
        return this.system;
    }

    protected abstract boolean canProcess();

    protected abstract Event doProcess(Event event);

    @Override
    public Future<Event> process(Event event) {
        if (canProcess()) {
            return future(new DoProcess(event), getSystem().dispatcher());
        } else {
            return Futures.successful(event);
        }
    }

    @Override
    public Future<List<Event>> processMultiple(List<Event> events) {
        if (canProcess()) {
            return future(new DoProcessMultiple(events), getSystem().dispatcher());
        } else {
            return Futures.successful((List<Event>) ImmutableList.copyOf(events));
        }
    }

    private class DoProcess implements Callable<Event> {
        private final Event event;

        public DoProcess(Event event) {
            this.event = event;
        }

        public Event call() throws Exception {
            return doProcess(event);
        }
    }

    private class DoProcessMultiple implements Callable<List<Event>> {
        private final List<Event> events;

        public DoProcessMultiple(List<Event> events) {
            this.events = events;
        }

        public List<Event> call() throws Exception {
            ImmutableList.Builder<Event> builder = new ImmutableList.Builder<Event>();
            for (Event event : this.events) {
                builder.add(doProcess(event));
            }
            return builder.build();
        }
    }
}
