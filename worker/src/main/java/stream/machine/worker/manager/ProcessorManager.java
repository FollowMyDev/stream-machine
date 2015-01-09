package stream.machine.worker.manager;

import io.dropwizard.lifecycle.Managed;
import stream.machine.core.processor.Processor;

/**
 * Created by Stephane on 08/01/2015.
 */
public class ProcessorManager implements Managed {
    private final Processor processor;

    public ProcessorManager(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void start() throws Exception {
        this.processor.start();
    }

    @Override
    public void stop() throws Exception {
        this.processor.stop();
    }

    public Processor getProcessor() {
        return this.getProcessor();
    }
}
