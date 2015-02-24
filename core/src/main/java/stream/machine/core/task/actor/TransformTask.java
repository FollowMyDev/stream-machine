package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import com.google.common.collect.ImmutableList;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import scala.concurrent.Future;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;

import java.io.StringWriter;
import java.util.List;
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
            return future(new DoProcess(configuration, event), system.dispatcher());
        } else {
            return Futures.successful(event);
        }
    }

    @Override
    public Future<List<Event>> processMultiple(List<Event> events) {
        if (engine != null) {
            return future(new DoProcessMultiple(configuration, events), system.dispatcher());
        } else {
            return Futures.successful(events);
        }
    }

    @Override
    public String getErrorField() {
        return "transformError";
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

    private Event transform(Event event, String template) {
        try {
            StringWriter writer = new StringWriter();

            VelocityContext context = new VelocityContext();
            context.put("event", event);

            this.engine.evaluate(context,
                    writer,
                    getName(),  // used for logging
                    template);

        } catch (Exception error) {
            event.put(getErrorField(), error.getMessage());
        }
        return event;

    }

    private class DoProcess implements Callable<Event> {
        private final String template;
        private final Event event;

        public DoProcess(EventTransformerConfiguration configuration, Event event) {
            this.template = configuration.getTemplate();
            this.event = event;
        }

        public Event call() throws Exception {
            return transform(this.event, this.template);
        }
    }

    private class DoProcessMultiple implements Callable<List<Event>> {
        private final String template;
        private final List<Event> events;

        public DoProcessMultiple(EventTransformerConfiguration configuration, List<Event> events) {

            this.template = configuration.getTemplate();
            this.events = events;
        }

        public List<Event> call() throws Exception {
            ImmutableList.Builder<Event> builder = new ImmutableList.Builder<Event>();
            for (Event event : this.events) {
                builder.add(transform(event, this.template));
            }
            return builder.build();
        }
    }
}
