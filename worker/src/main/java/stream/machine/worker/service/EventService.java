package stream.machine.worker.service;

import akka.util.Timeout;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.worker.Worker;
import stream.machine.core.worker.WorkerType;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventService {
    private final Worker transformWorker;
    private final int timeoutInSeconds;
    protected final Logger logger;

    public EventService(StreamManager streamManager, int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        if (streamManager != null) {
            this.transformWorker = streamManager.getWorker("EventService", WorkerType.Transform);
        } else {
            this.transformWorker = null;
        }
        logger = LoggerFactory.getLogger("EventService");
    }

    @PUT
    @Timed
    @Path("/transform")
    public Event transform(Event event) throws ApplicationException {
        if (transformWorker != null ) {
            Timeout timeout = new Timeout(Duration.create(timeoutInSeconds, "seconds"));
            try {
                return Await.result(transformWorker.transform(event),timeout.duration());
            } catch (Exception error) {
                logger.error("Failed to  transform event",error);
                throw new ApplicationException("Failed to  transform event",error);
            }
        }
        return event;
    }

}

