package stream.machine.core.task;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.SequenceConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Stephane on 22/02/2015.
 */
public class SequenceTask extends TaskBase {
    private List<Task> tasks;

    public SequenceTask(Configuration configuration, TaskFactory taskFactory, ExecutorService executor) {
        super(configuration, executor);
        SequenceConfiguration sequenceConfiguration = new SequenceConfiguration(configuration);
        if (sequenceConfiguration != null && sequenceConfiguration.getTaskConfigurations() != null && taskFactory != null) {
            ImmutableList.Builder<Task> builder = new ImmutableList.Builder<Task>();
            try {
                for (String taskConfiguration : sequenceConfiguration.getTaskConfigurations().values()) {
                    builder.add(taskFactory.build(taskConfiguration));
                }
                tasks = builder.build();
            } catch (ApplicationException error) {
                logger.error("Cannot create sequence", error);
            }
        }
    }

    @Override
    protected boolean canProcess() {
        return true;
    }

    @Override
    protected Event doProcess(Event event) {
        return event;
    }

    @Override
    public ListenableFuture<Event> process(Event event) {
        if (tasks != null) {
            ListenableFuture<Event> sequence = Futures.immediateFuture(event);
            for (final Task task : this.tasks) {
                AsyncFunction<Event, Event> sequenceTask = new AsyncFunction<Event, Event>() {
                    public ListenableFuture<Event> apply(Event currentEvent) {
                        return task.process(currentEvent);
                    }
                };
                sequence = Futures.transform(sequence, sequenceTask, getExecutor());
            }
            return sequence;
        } else {
            return Futures.immediateFuture(event);
        }
    }


    @Override
    public ListenableFuture<List<Event>> processMultiple(List<Event> events) {
        if (tasks != null) {
            ListenableFuture<List<Event>> sequence = Futures.immediateFuture(events);
            for (final Task task : this.tasks) {
                AsyncFunction<List<Event>, List<Event>> sequenceTask = new AsyncFunction<List<Event>, List<Event>>() {
                    public ListenableFuture<List<Event>> apply(List<Event> currentEvents) {
                        return task.processMultiple(currentEvents);
                    }
                };
                sequence = Futures.transform(sequence, sequenceTask, getExecutor());
            }
            return sequence;
        } else {
            return Futures.immediateFuture(events);
        }
    }

    @Override
    public String getErrorField() {
        return "sequenceError";
    }

    @Override
    public void start() throws ApplicationException {
        if (tasks != null) {
            for (Task task : this.tasks) {
                if (task != null) {
                    task.start();
                }
            }
        }
    }

    @Override
    public void stop() throws ApplicationException {
        if (tasks != null) {
            for (Task task : this.tasks) {
                if (task != null) {
                    task.stop();
                }
            }
        }
    }
}
