package stream.machine.worker.manager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import io.dropwizard.lifecycle.Managed;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.message.DataMessage;
import stream.machine.core.message.ErrorMessage;
import stream.machine.core.message.Message;
import stream.machine.core.model.Event;
import stream.machine.core.task.TaskManager;
import stream.machine.core.task.TaskStatus;
import stream.machine.core.task.store.ConfigurationStore;

/**
 * Created by Stephane on 08/01/2015.
 */
public class AgentManager extends ManageableBase implements Managed {
    private final ConfigurationStore configurationStore;
    private final ActorRef taskManager;
    private final ActorSystem system;
    private final int timeoutInSeconds;

    public AgentManager(ConfigurationStore configurationStore, int timeoutInSeconds) {
        super("AgentManger");
        this.configurationStore = configurationStore;
        this.system = ActorSystem.create("TransformTasks");
        this.taskManager = system.actorOf(TaskManager.props("TaskManager", configurationStore,timeoutInSeconds));
        this.timeoutInSeconds =timeoutInSeconds;
    }

    @Override
    public void start() {
      logger.info("Starting agent manager ...");
      logger.info(" ... agent manager started");
    }

    @Override
    public void stop() {
        logger.info("Stopping agent manager ...");
        system.shutdown();
        logger.info(" ... agent manager stopped");
    }

    public void onEvent(Event event) throws ApplicationException {
        logger.debug(String.format("Start processing event %s ...", event.getKey().toString()));
        Timeout timeout = new Timeout(Duration.create(this.timeoutInSeconds, "seconds"));
        Message query = new DataMessage<Event>("TaskManager",event);
        query.getStatusTable().setStatus("TaskManager", TaskStatus.INITIAL);
        Future<Object> future= Patterns.ask(taskManager, query, timeout);
        try {
            Message reply = (DataMessage<Event>) Await.result(future, timeout.duration());
            if (reply instanceof ErrorMessage)
            {
                ErrorMessage errorMessage = (ErrorMessage) reply;
                throw new ApplicationException(String.format("Processing of event failed %s %s", event.getKey().toString(), errorMessage.getErrorMessage()));
            }
        } catch (Exception error) {
            throw new ApplicationException(String.format("Processing of event failed %s %s", event.getKey().toString(), error));

        }
        logger.debug(String.format("... stop  processing event %s", event.getKey().toString()));
    }
}
