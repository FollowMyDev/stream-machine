package stream.machine.core.task;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.TransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.io.StringWriter;
import java.util.concurrent.ExecutorService;

/**
 * Created by Stephane on 07/12/2014.
 */
public class TransformTask extends TaskBase {
    private VelocityEngine engine;
    private final String template;

    public TransformTask(Configuration configuration, ExecutorService executor) {
        super(configuration, executor);
        TransformerConfiguration transformerConfiguration = new TransformerConfiguration(configuration);
        this.template = transformerConfiguration.getTemplate();
    }

    @Override
    protected boolean canProcess() {
        return (engine != null);
    }

    @Override
    protected Event doProcess(Event event) {
        try {
            StringWriter writer = new StringWriter();
            VelocityContext context = new VelocityContext();
            context.put("event", event);
            engine.evaluate(context,
                    writer,
                    getName(),  // used for logging
                    template);

        } catch (Exception error) {
            event.put(getErrorField(), error.getMessage());
        }
        return event;
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


}
