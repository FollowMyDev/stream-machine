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
    private ActorRef task;
    protected LoggingAdapter logger = Logging.getLogger(getContext().system(), this);


    //todo: add timeout to configuration
    public TaskManager(String name, ConfigurationStore configurationStore) {
        this.configurationStore = configurationStore;
        this.name = name;
        getContext().setReceiveTimeout(Duration.create("300 seconds"));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        TaskConfiguration taskConfiguration = this.configurationStore.read(name);
        this.task = getContext().actorOf(Task.props(taskConfiguration, getSelf()), taskConfiguration.getName());
        getContext().watch(this.task);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void onReceive(Object message) throws ApplicationException {
        if (message instanceof Terminated) {
            //todo: manage child termination
            return;
        }
        if (message instanceof Timeout) {
            //todo: manage timeout message => cancel mode + alert
            return;
        }
        if (message instanceof DataMessage) {
            DataMessage dataMessage = (DataMessage) message;
            if (dataMessage.getType() == MessageType.QUERY) {
                dataMessage.setTask(this.name);
                 this.task.forward(dataMessage, getContext());
            }
            if (dataMessage.getType() == MessageType.REPLY) {
                TaskStatus status  =   dataMessage.getStatusTable().getStatus(dataMessage.getTask());
                switch (status) {
                    case DONE:
                        dataMessage.getStatusTable().setStatus(dataMessage.getTask(), TaskStatus.COMPLETE);
                        getSender().tell(message, getSelf());
                        break;
                    case ERROR:
                        throw new ApplicationException(dataMessage.getErrorTable().getError(dataMessage.getTask()));
                    case UNDEFINED:
                        throw new ApplicationException("The processing has failed");
                }
            }
        }

        if (message instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) message;
            throw new ApplicationException(errorMessage.getErrorMessage());
        }
    }

    public void process(Event event)  throws ApplicationException
    {
        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Future<Object> future= Patterns.ask(this.getSelf(), event, timeout);
        try {
            DataMessage<Event> result = (DataMessage<Event>) Await.result(future, timeout.duration());
        } catch (Exception error) {
            throw new ApplicationException("Event processing failed", error);
        }
    }
}
