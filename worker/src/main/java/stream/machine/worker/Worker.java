package stream.machine.worker;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import stream.machine.core.extension.ExtensionManager;
import stream.machine.core.processor.Processor;
import stream.machine.core.task.store.ConfigurationStore;
import stream.machine.worker.configuration.WorkerConfiguration;
import stream.machine.worker.manager.ProcessorManager;
import stream.machine.worker.service.WorkerService;

/**
 * Created by Stephane on 07/01/2015.
 */
public class Worker extends Application<WorkerConfiguration> {

    public static void main(String[] args) throws Exception {
        new Worker().run(args);
    }


    @Override
    public void initialize(Bootstrap<WorkerConfiguration> bootstrap) {

    }

    @Override
    public void run(WorkerConfiguration configuration, Environment environment) throws Exception {
        ExtensionManager extensionManager = new ExtensionManager();
        extensionManager.load();
        ConfigurationStore store = extensionManager.getConfigurationStore("elasticsearch.plugin.task.store.configuration.Store");
        ProcessorManager processorManager = new ProcessorManager(new Processor());
        final WorkerService workerService = new WorkerService(processorManager);
        environment.jersey().register(workerService);
        environment.lifecycle().manage(processorManager);
    }
}