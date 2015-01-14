package stream.machine.core.task;

import akka.actor.ActorRef;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TransformerTaskConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.task.transform.TransformerTask;

/**
 * Created by Stephane on 14/01/2015.
 */
public class ExceptionTask extends TransformerTask<Event, Event> {

    private final String error;

    protected ExceptionTask(TaskConfiguration configuration, ActorRef superTask, ActorRef subTask) {
        super(configuration, superTask, subTask);

        TransformerTaskConfiguration errorConfiguration = (TransformerTaskConfiguration) configuration;
        this.error = errorConfiguration.getTemplate();
    }

    @Override
    protected Event transform(Event data) throws ApplicationException {
        throw new ApplicationException(this.error);
    }
}
