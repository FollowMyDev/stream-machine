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

    public static Props props(final TaskConfiguration configuration, final ActorRef superTask) {
        return Props.create(new Creator<Task>() {
            @Override
            public Task create() throws Exception {
                Class<Task> taskClass = (Class<Task>) Class.forName(configuration.getTaskClass());
                Constructor<Task> ctor = taskClass.getDeclaredConstructor(TaskConfiguration.class, ActorRef.class);
                Task task = ctor.newInstance(configuration, superTask);
                return task;
            }
        });
    }

    private final String name;
    private final ActorRef superTask;
    private final Map<String, ActorRef> subTasks;
    protected LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    protected Task(TaskConfiguration configuration, ActorRef superTask) {
        this.name = configuration.getName();
        this.superTask = superTask;
        this.subTasks = new HashMap<String, ActorRef>();
        if (configuration.getSubTasks() != null) {
            for (TaskConfiguration subTaskConfiguration : configuration.getSubTasks()) {
                ActorRef subTask = getContext().actorOf(Task.props(subTaskConfiguration, getSelf()), subTaskConfiguration.getName());
                this.subTasks.put(subTaskConfiguration.getName(), subTask);
            }
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        logger.debug("Received message");

        if (message instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) message;
            TaskStatus currentStatus = errorMessage.getStatusTable().getStatus(getName());
            if (currentStatus != TaskStatus.ERROR) {
                errorMessage.getStatusTable().setStatus(getName(), TaskStatus.ERROR);
                String error = "A sub task has failed";
                errorMessage.getErrorTable().setError(getName(), error);
                sendUp(errorMessage);
            }
        }

        if (message instanceof DataMessage) {
            if (((Message) message).getType() == MessageType.QUERY) {
                DataMessage query = (DataMessage) message;
                Message reply = processMessage(query);
                TaskStatus status = reply.getStatusTable().getStatus(getName());
                if (status == TaskStatus.DONE) {
                    if (this.subTasks.size() > 0) {
                        // Sub tasks are allowed to process
                        sendDown(query);
                    } else {
                        // Notify super task of the success
                        sendUp(reply);
                    }

                } else {
                    // Notify super task of the failure
                    sendUp(reply);
                }
            }
            if (((DataMessage) message).getType() == MessageType.REPLY) {
                DataMessage reply = ((DataMessage) message);
                reply.getStatusTable().setStatus(reply.getTask(), TaskStatus.COMPLETE);
                if (isComplete(reply.getStatusTable())) {
                    reply.setTask(getName());
                    sendUp(reply);
                }
            }
        }
    }

    protected boolean isComplete(StatusTable statusTable) throws ApplicationException {
        int subTaskCount = this.subTasks.size();
        int subTaskComplete = 0;
        if (subTaskCount > 0) {
            for (String subTask : this.subTasks.keySet()) {
                TaskStatus subTaskStatus = statusTable.getStatus(subTask);
                switch (subTaskStatus) {
                    case COMPLETE:
                        subTaskComplete++;
                        break;
                }
            }
        }
        return (subTaskComplete == subTaskCount);
    }

    private Message processMessage(Message query) {
        query.getStatusTable().setStatus(getName(), TaskStatus.PROCESSING);
        try {
            Message reply = doProcess(query);
            query.getStatusTable().setStatus(getName(), TaskStatus.DONE);
            return reply;
        } catch (ApplicationException error) {
            ErrorMessage reply = new ErrorMessage(getName(),query,error.getMessage());
            reply.getStatusTable().setStatus(getName(), TaskStatus.ERROR);
            reply.getErrorTable().setError(getName(), error.getMessage());
            return reply;
        }

    }

    private void sendUp(Message message) {
        ActorRef sender = (this.superTask == null)? getSender(): this.superTask;
        sender.forward(message, getContext());
    }

    private void sendDown(Message message) {
        for (Map.Entry<String, ActorRef> subTask : this.subTasks.entrySet()) {
            message.getStatusTable().setStatus(subTask.getKey(), TaskStatus.INITIAL);
            subTask.getValue().forward(message, getContext());
        }
    }

    protected abstract Message doProcess(Message message) throws ApplicationException;
}
