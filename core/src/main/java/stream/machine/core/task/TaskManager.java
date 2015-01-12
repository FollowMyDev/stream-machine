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
import stream.machine.core.configuration.task.TaskConfiguration;
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


    public static Props props(final String name, final ConfigurationStore configurationStore, final int timeoutInSeconds) {
        return Props.create(new Creator<TaskManager>() {
            @Override
            public TaskManager create() throws Exception {
                return new TaskManager(name, configurationStore,timeoutInSeconds);
            }
        });
    }

    private final ConfigurationStore configurationStore;
    private final String name;
    private final int timeoutInSeconds;
    private ActorRef task;
    protected LoggingAdapter logger = Logging.getLogger(getContext().system(), this);


    public TaskManager(String name, ConfigurationStore configurationStore, int timeoutInSeconds) {
        this.configurationStore = configurationStore;
        this.name = name;
        this.timeoutInSeconds = timeoutInSeconds;
        Timeout timeout = new Timeout(Duration.create(this.timeoutInSeconds, "seconds"));
        getContext().setReceiveTimeout(timeout.duration());
    }

    @Override
    public void preStart() throws Exception {
        logger.info("Starting task manager ...");
        super.preStart();
        TaskConfiguration taskConfiguration = this.configurationStore.read(name);
        this.task = getContext().actorOf(Task.props(taskConfiguration, getSelf()), taskConfiguration.getName());
        getContext().watch(this.task);
        logger.info(" ... Task manager started");
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
                this.task.forward(dataMessage, getContext());
            }
            if (dataMessage.getType() == MessageType.REPLY) {
                TaskStatus status = dataMessage.getStatusTable().getStatus(dataMessage.getTask());
                switch (status) {
                    case DONE:
                        dataMessage.getStatusTable().setStatus(dataMessage.getTask(), TaskStatus.COMPLETE);
                        getSender().tell(message, getSelf());
                        break;
                    case ERROR:
                        ErrorMessage errorMessage = new ErrorMessage(dataMessage.getTask(), dataMessage.getErrorTable().getError(dataMessage.getTask()));
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
