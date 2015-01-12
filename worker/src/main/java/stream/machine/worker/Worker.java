package stream.machine.worker;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.extension.ExtensionManager;
import stream.machine.core.task.store.ConfigurationStore;
import stream.machine.worker.configuration.WorkerConfiguration;
import stream.machine.worker.manager.AgentManager;
import stream.machine.worker.service.WorkerService;

/**
 * Created by Stephane on 07/01/2015.
 */
public class Worker extends Application<WorkerConfiguration> {

    private ExtensionManager extensionManager;
    private Logger logger;

    public Worker() {
        logger = LoggerFactory.getLogger("Worker");
        extensionManager = new ExtensionManager();
    }

    public static void main(String[] args) throws Exception {
        new Worker().run(args);
    }


    @Override
    public void initialize(Bootstrap<WorkerConfiguration> bootstrap) {

    }

    @Override
    public void run(WorkerConfiguration configuration, Environment environment) throws Exception {
        try {
            extensionManager.start();
            ConfigurationStore configurationStore = extensionManager.getConfigurationStore(configuration.getConfigurationStore());
            AgentManager agentManager = new AgentManager(configurationStore, configuration.getTimeoutInSeconds());
            final WorkerService workerService = new WorkerService(agentManager);
            environment.jersey().register(workerService);
            environment.lifecycle().manage(agentManager);
        } catch (ApplicationException error) {
            logger.error("Cannot load plugins!!!",error);
            this.extensionManager=null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        extensionManager.stop();
    }
}