package stream.machine.worker.service;

import com.codahale.metrics.annotation.Timed;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.worker.manager.AgentManager;

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

    private AgentManager manager;


    public WorkerService(AgentManager manager) {
        this.manager = manager;
    }

    @PUT
    @Timed
    @Path("/process")
    public void onEvent(Event event) throws ApplicationException {
        this.manager.onEvent(event);
    }
}
