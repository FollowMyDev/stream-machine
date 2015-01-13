package stream.machine.core.task;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.task.TaskChainConfiguration;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TaskManagerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.message.DataMessage;
import stream.machine.core.message.ErrorMessage;
import stream.machine.core.message.MessageType;
import stream.machine.core.model.Event;
import stream.machine.core.task.store.ConfigurationStore;


/**
 * Created by Stephane on 03/01/2015.
 */
public class TaskManager extends UntypedActor {

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"),
                    new Function<Throwable, SupervisorStrategy.Directive>() {
                        @Override
                        public SupervisorStrategy.Directive apply(Throwable error) {
                            if (error instanceof ApplicationException) {
                                return SupervisorStrategy.resume();
                            } else {
                                return SupervisorStrategy.escalate();
                            }
                        }
                    });


    public static Props props(final String name, final ConfigurationStore configurationStore) {
        return Props.create(new Creator<TaskManager>() {
            @Override
            public TaskManager create() throws Exception {
                return new TaskManager(name, configurationStore);
            }
        });
    }

    private final ConfigurationStore configurationStore;
    private final String name;
    private ActorRef subTask;
    protected LoggingAdapter logger = Logging.getLogger(getContext().system(), this);


    public TaskManager(String name, ConfigurationStore configurationStore) {
        this.configurationStore = configurationStore;
        this.name = name;

    }

    @Override
    public void preStart() throws Exception {

        logger.info("Starting task manager ...");
        super.preStart();

        TaskManagerConfiguration taskManagerConfiguration = this.configurationStore.readTaskManager(name);
        if (taskManagerConfiguration != null) {
            TaskChainConfiguration taskChain = taskManagerConfiguration.getSubTasks();
            if (taskChain != null) {
                ActorRef subTask = buildSubTask(taskChain);
                this.subTask = subTask;
                Timeout timeout = new Timeout(Duration.create(taskManagerConfiguration.getTimeoutInSeconds(), "seconds"));
                getContext().setReceiveTimeout(timeout.duration());
            } else {
                logger.error(String.format("Cannot find configuration for task manager %s", name));
                throw new ApplicationException(String.format("Cannot find configuration for task manager %s", name));
            }
            logger.info(" ... Task manager started");
        }
    }

    private ActorRef buildSubTask(TaskChainConfiguration taskChainConfiguration) throws ApplicationException {
        if (taskChainConfiguration == null) return null;

        String subTaskName = taskChainConfiguration.getName();
        TaskConfiguration subTaskConfiguration = this.configurationStore.readTask(subTaskName);
        if (subTaskConfiguration == null) {
            logger.error(String.format("Cannot find configuration for task %s", subTaskName));
            throw new ApplicationException(String.format("Cannot find configuration for task %s", subTaskName));
        }

        ActorRef subTask = getContext().actorOf(Task.props(subTaskConfiguration, getSelf(), buildSubTask(taskChainConfiguration.getSubTask())), subTaskConfiguration.getName());
        getContext().watch(subTask);
        return subTask;
    }

    @Override
    public void postStop() throws Exception {
        logger.info("Stopping task manager ...");
        super.postStop();
        logger.info(" ... Task manager stopped");
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void onReceive(Object message) throws ApplicationException {
        logger.debug("Message received ... ");
        if (message instanceof Terminated) {
            //todo: manage child termination
            logger.debug("... message processed");
            return;
        }
        if (message instanceof Timeout) {
            ErrorMessage errorMessage = new ErrorMessage("TaskManager", "The message has timed out");
            getSender().tell(errorMessage, getSelf());
            logger.debug("... message processed");
            return;
        }
        if (message instanceof DataMessage) {
            DataMessage dataMessage = (DataMessage) message;
            if (dataMessage.getType() == MessageType.QUERY) {
                dataMessage.setTask(this.name);
                this.subTask.forward(dataMessage, getContext());
            }
            if (dataMessage.getType() == MessageType.REPLY) {
                TaskStatus status = dataMessage.getStatus();
                switch (status) {
                    case DONE:
                        getSender().tell(message, getSelf());
                        break;
                    case ERROR:
                        ErrorMessage errorMessage = new ErrorMessage(dataMessage.getTask(), String.format("Sub task %s has failed!", dataMessage.getTask()));
                        getSender().tell(errorMessage, getSelf());
                        break;
                    case UNDEFINED:
                        ErrorMessage undefinedMessage = new ErrorMessage("TaskManager", "The processing has failed for unknown reasons");
                        getSender().tell(undefinedMessage, getSelf());
                        break;
                }
            }
            if (message instanceof ErrorMessage) {
                ErrorMessage errorMessage = (ErrorMessage) message;
                getSender().tell(errorMessage, getSelf());
            }
            logger.debug("... message processed");
        }
    }
}
