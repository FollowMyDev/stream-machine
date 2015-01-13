package stream.machine.core.task.transform;

import akka.actor.ActorRef;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.message.DataMessage;
import stream.machine.core.message.Message;
import stream.machine.core.task.Task;

/**
 * Created by Stephane on 07/12/2014.
 */
public abstract class TransformerTask<Q, R> extends Task {
    protected TransformerTask(TaskConfiguration configuration, ActorRef superTask, ActorRef subTask) {
        super(configuration, superTask, subTask);
    }

    protected abstract R transform(final Q data) throws ApplicationException;

    @Override
    protected Message doProcess(Message message) throws ApplicationException {
        DataMessage<Q> query = (DataMessage<Q>) message;
        R result = transform(query.getData());
        return new DataMessage<R>(getName(), message, result);
    }

}
