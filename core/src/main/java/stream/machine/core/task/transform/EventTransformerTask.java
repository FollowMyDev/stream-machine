package stream.machine.core.task.transform;

import akka.actor.ActorRef;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TransformerConfiguration;
import stream.machine.core.model.Event;

import java.io.StringWriter;

/**
 * Created by Stephane on 07/12/2014.
 */
public class EventTransformerTask extends TransformerTask<Event,Event> {


    private VelocityEngine engine;
    private final String template;


    public EventTransformerTask(TaskConfiguration configuration,ActorRef superTask) {
        super(configuration,superTask);
        TransformerConfiguration transformerConfiguration = (TransformerConfiguration) configuration;
        this.template = transformerConfiguration.getTemplate();
    }

    @Override
    protected Event transform(Event event) {

        StringWriter writer = new StringWriter();

        VelocityContext context = new VelocityContext();
        context.put("event", event);

        engine.evaluate(context,
                writer,
                "LOG",  // used for logging
                template);
         return event;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        engine = new VelocityEngine();
        engine.init();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }
}
