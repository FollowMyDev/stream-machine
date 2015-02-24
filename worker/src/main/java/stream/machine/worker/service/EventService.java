package stream.machine.worker.service;

import akka.util.Timeout;
import com.codahale.metrics.annotation.Timed;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskType;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventService extends ManageableBase {
    private Map<String, Task> transformWorkers;
    private final int timeoutInSeconds;

    public EventService(StreamManager streamManager, int timeoutInSeconds) {
        super("EventService");
        this.timeoutInSeconds = timeoutInSeconds;
        if (streamManager != null) {
            try {
                this.transformWorkers = streamManager.getTasks(TaskType.Transform);
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        } else {
            this.transformWorkers = null;
        }

    }

    @POST
    @Timed
    @Path("/transform/{transformer}")
    public Event transform(@PathParam("transformer") String transformer, Event event) throws ApplicationException {
        if (transformWorkers != null) {
            Timeout timeout = new Timeout(Duration.create(timeoutInSeconds, "seconds"));
            try {
                Task transformWorker = this.transformWorkers.get(transformer);
                if (transformWorker != null) {
                    return Await.result(transformWorker.process(event), timeout.duration());
                }
            } catch (Exception error) {
                logger.error("Failed to  process event", error);
                throw new ApplicationException("Failed to  process event", error);
            }
        }
        return event;
    }

    @Override
    public void start() throws ApplicationException {
        if (transformWorkers != null) {
            for (Task transformWorker : this.transformWorkers.values()) {
                transformWorker.start();
            }
        }
    }

    @Override
    public void stop() throws ApplicationException {
        if (transformWorkers != null) {
            for (Task transformWorker : this.transformWorkers.values()) {
                transformWorker.stop();
            }
        }
    }
}

