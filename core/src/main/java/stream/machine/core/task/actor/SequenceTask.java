package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.japi.Function2;
import com.google.common.collect.ImmutableList;
import scala.concurrent.Future;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.MapperConfiguration;
import stream.machine.core.configuration.SequenceConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;

import java.util.List;

/**
 * Created by Stephane on 22/02/2015.
 */
public class SequenceTask extends TaskBase {
    private List<Task> tasks;

    public SequenceTask(Configuration configuration, TaskFactory taskFactory, ActorSystem system) {
        super(configuration, system);
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
    public Future<Event> process(Event event) {

        if (tasks != null) {
            ImmutableList.Builder<Future<Event>> builder = new ImmutableList.Builder<Future<Event>>();
            for (Task task : this.tasks) {
                builder.add(task.process(event));
            }
            return Futures.fold(event, builder.build(), new Function2<Event, Event, Event>() {
                public Event apply(Event before, Event after) {
                    return after;
                }
            }, getSystem().dispatcher());
        } else {
            return Futures.successful(event);
        }
    }


    @Override
    public Future<List<Event>> processMultiple(List<Event> events) {
        if (tasks != null) {
            ImmutableList.Builder<Future<List<Event>>> builder = new ImmutableList.Builder<Future<List<Event>>>();
            for (Task task : this.tasks) {
                builder.add(task.processMultiple(events));
            }
            return Futures.fold(events, builder.build(), new Function2<List<Event>, List<Event>, List<Event>>() {
                public List<Event> apply(List<Event> before, List<Event> after) {
                    return after;
                }
            }, getSystem().dispatcher());
        } else {
            return Futures.successful(events);
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
