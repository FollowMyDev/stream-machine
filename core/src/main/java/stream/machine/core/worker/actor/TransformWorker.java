package stream.machine.core.worker.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.japi.Function2;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import scala.concurrent.Future;
import stream.machine.core.configuration.task.EventTransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.worker.Worker;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static akka.dispatch.Futures.fold;
import static akka.dispatch.Futures.future;

/**
 * Created by Stephane on 07/12/2014.
 */
public class TransformWorker extends ManageableBase implements Worker {


    private VelocityEngine engine;
    private final EventTransformerConfiguration configuration;
    private final ActorSystem system;


    public TransformWorker(EventTransformerConfiguration configuration, ActorSystem system) {
        super(configuration.getName());
        this.configuration = configuration;
        this.system = system;
    }

    @Override
    public Future<Event> transform(Event event) {
        if (engine != null) {
            return future(new DoTransform(engine, configuration, event), system.dispatcher());
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


    public class DoTransform implements Callable<Event> {
        private final String template;
        private final Event event;
        private VelocityEngine engine;

        public DoTransform(VelocityEngine engine, EventTransformerConfiguration configuration, Event event) {
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
