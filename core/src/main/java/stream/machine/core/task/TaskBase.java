package stream.machine.core.task;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


/**
 * Created by Stephane on 27/02/2015.
 */
public abstract class TaskBase extends ManageableBase implements Task {
    private final ListeningExecutorService executor;

    protected TaskBase(Configuration configuration, ExecutorService executor) {
        super(configuration.getName());
        this.executor =  MoreExecutors.listeningDecorator(executor);;
    }

    protected ListeningExecutorService getExecutor() {
        return this.executor;
    }

    protected abstract boolean canProcess();

    protected abstract Event doProcess(Event event);

    @Override
    public ListenableFuture<Event> process(Event event) {
        if (canProcess()) {
            return executor.submit(new DoProcess(event));
        } else {
            return Futures.immediateFuture(event);
        }
    }

    @Override
    public ListenableFuture<List<Event>> processMultiple(List<Event> events) {
        if (canProcess()) {
            return  executor.submit(new DoProcessMultiple(events));
        } else {
            return Futures.immediateFuture(events);
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
