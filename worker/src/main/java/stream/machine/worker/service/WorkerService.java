package stream.machine.worker.service;

import akka.actor.ActorRef;
import com.codahale.metrics.annotation.Timed;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.task.TaskManager;
import stream.machine.worker.manager.ProcessorManager;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Stephane on 29/12/2014.
 */

@Path("/agent")
@Produces(MediaType.APPLICATION_JSON)
public class WorkerService {

    private ProcessorManager manager;


    public WorkerService(ProcessorManager manager) {
        this.manager = manager;
    }

    @PUT
    @Timed
    @Path("/process")
    public void onEvent(Event event) throws ApplicationException {
        this.manager.getProcessor().process(event);
    }
}
