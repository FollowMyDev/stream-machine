package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import scala.concurrent.Future;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;

import java.io.StringWriter;
import java.util.concurrent.Callable;

import static akka.dispatch.Futures.future;

/**
 * Created by Stephane on 07/12/2014.
 */
public class TransformTask extends ManageableBase implements Task {
    private VelocityEngine engine;
    private final EventTransformerConfiguration configuration;
    private final ActorSystem system;

    public TransformTask(EventTransformerConfiguration configuration, ActorSystem system) {
        super(configuration.getName());
        this.configuration = configuration;
        this.system = system;
    }

    @Override
    public Future<Event> process(Event event) {
        if (engine != null) {
            return future(new DoProcess(engine, configuration, event), system.dispatcher());
        } else {
            return Futures.successful(event);
        }
    }

    @Override
    public void start() throws ApplicationException {
        engine = new VelocityEngine();
        engine.init();
    }

    @Override
    public void stop() throws ApplicationException {
        engine = null;
    }

    private class DoProcess implements Callable<Event> {
        private final String template;
        private final Event event;
        private final VelocityEngine engine;

        public DoProcess(VelocityEngine engine, EventTransformerConfiguration configuration, Event event) {
            this.engine = engine;
            this.template = configuration.getTemplate();
            this.event = event;
        }

        public Event call() throws Exception {
            StringWriter writer = new StringWriter();

            VelocityContext context = new VelocityContext();
            context.put("event", this.event);

            this.engine.evaluate(context,
                    writer,
                    "LOG",  // used for logging
                    template);

            return event;
        }
    }

}
