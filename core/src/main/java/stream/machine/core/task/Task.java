package stream.machine.core.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.message.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephane on 03/01/2015.
 */
public abstract class Task extends UntypedActor {

    public static Props props(final TaskConfiguration configuration, final ActorRef superTask, final ActorRef subTask) {
        return Props.create(new Creator<Task>() {
            @Override
            public Task create() throws Exception {
                Class<Task> taskClass = (Class<Task>) Class.forName(configuration.getTaskClass());
                Constructor<Task> ctor = taskClass.getDeclaredConstructor(TaskConfiguration.class, ActorRef.class, ActorRef.class);
                Task task = ctor.newInstance(configuration, superTask, subTask);
                return task;
            }
        });
    }

    private final String name;
    private final ActorRef superTask;
    private final ActorRef subTask;
    protected LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    protected Task(TaskConfiguration configuration, ActorRef superTask, ActorRef subTask) {
        this.name = configuration.getName();
        this.superTask = superTask;
        this.subTask = subTask;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        logger.debug("Received message");

        if (message instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) message;
            errorMessage.setTask(this.name);
            sendError(errorMessage);
        }

        if (message instanceof DataMessage) {
            if (((Message) message).getType() == MessageType.QUERY) {
                DataMessage query = (DataMessage) message;
                Message reply = processMessage(query);
                TaskStatus status = reply.getStatus();
                if (status == TaskStatus.DONE) {
                    if (this.subTask != null) {
                        // Sub task is allowed to process
                        sendDown(query);
                    } else {
                        // Notify super task of the success
                        sendUp(reply);
                    }
                } else {
                    if (reply instanceof ErrorMessage) {
                        ErrorMessage errorMessage = (ErrorMessage) reply;
                        errorMessage.setTask(this.name);
                        sendError(errorMessage);
                    }
                    else {
                       unhandled(message);
                    }
                }
            }
            if (((DataMessage) message).getType() == MessageType.REPLY) {
                DataMessage reply = ((DataMessage) message);
                reply.setTask(this.name);
                sendUp(reply);
            }
        }
    }


    private Message processMessage(Message query) {
        try {
            query.setStatus(TaskStatus.PROCESSING);
            Message reply = doProcess(query);
            reply.setStatus(TaskStatus.DONE);
            return reply;
        } catch (ApplicationException error) {
            ErrorMessage reply = new ErrorMessage(getName(), query, error.getMessage());
            return reply;
        }
    }

    private void sendUp(Message message) {
        ActorRef superTask = (this.superTask == null) ? getSender() : this.superTask;
        message.setTask(this.name);
        superTask.forward(message, getContext());
    }

    private void sendError(ErrorMessage message) {
        getSender().tell(message, getSelf());
    }

    private void sendDown(Message message) {
        if (this.subTask != null) {
            message.setTask(this.name);
            subTask.forward(message, getContext());
        }
    }

    protected abstract Message doProcess(Message message) throws ApplicationException;
}
