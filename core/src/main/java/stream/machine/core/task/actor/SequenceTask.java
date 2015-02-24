package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.japi.Function2;
import com.google.common.collect.ImmutableList;
import scala.concurrent.Future;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.sequence.SequenceConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;

import java.util.List;

/**
 * Created by Stephane on 22/02/2015.
 */
public class SequenceTask extends ManageableBase implements Task {
    private List<Task> tasks;
    private final ActorSystem system;

    public SequenceTask(String name, SequenceConfiguration sequenceConfiguration, TaskFactory taskFactory, ActorSystem system) {
        super(name);
        if (sequenceConfiguration != null && sequenceConfiguration.getTaskConfigurations() != null && taskFactory != null) {
            ImmutableList.Builder<Task> builder = new ImmutableList.Builder<Task>();
            try {
                for (Configuration configuration : sequenceConfiguration.getTaskConfigurations().values()) {
                    builder.add(taskFactory.build(configuration.getType(), configuration.getName()));
                }
                tasks = builder.build();
            } catch (ApplicationException error) {
                logger.error("Cannot create sequence", error);
            }
        }

        this.system = system;
    }

    @Override
    public Future<Event> process(Event event) {

        if (tasks != null) {
            ImmutableList.Builder<Future<Event>> builder = new ImmutableList.Builder<Future<Event>>();
            for (Task task : this.tasks) {
                builder.add(task.process(event));
            }
            return Futures.fold(event, builder.build(), new Function2<Event, Event, Event>() {
                public Event apply(Event r, Event t) {
                    return t;
                }
            }, system.dispatcher());
        } else {
            return Futures.successful(event);
        }
    }


    @Override
    public Future<List<Event>> processMultiple(List<Event> events) {
        return null;
    }

    @Override
    public String getErrorField() {
        return null;
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
