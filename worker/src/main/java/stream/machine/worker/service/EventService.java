package stream.machine.worker.service;

import akka.util.Timeout;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.worker.Worker;
import stream.machine.core.worker.WorkerType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventService extends ManageableBase{
    private final Worker transformWorker;
    private final int timeoutInSeconds;
    protected final Logger logger;

    public EventService(StreamManager streamManager, int timeoutInSeconds) {
        super("EventService");
        this.timeoutInSeconds = timeoutInSeconds;
        if (streamManager != null) {
            this.transformWorker = streamManager.getWorker("AplusBinC", WorkerType.Transform);
        } else {
            this.transformWorker = null;
        }
        logger = LoggerFactory.getLogger("EventService");
    }

    @GET
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

    @Override
    public void start() throws ApplicationException {
        if ( transformWorker != null) {
            this.transformWorker.start();
        }
    }

    @Override
    public void stop() throws ApplicationException {
        if ( transformWorker != null) {
            this.transformWorker.stop();
        }
    }
}

