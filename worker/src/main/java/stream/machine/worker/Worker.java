package stream.machine.worker;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.machine.core.extension.ExtensionManager;
import stream.machine.core.stream.StreamManager;
import stream.machine.worker.configuration.WorkerConfiguration;
import stream.machine.worker.service.ConfigurationService;
import stream.machine.worker.service.EventService;


/**
 * Created by Stephane on 07/01/2015.
 */
public class Worker extends Application<WorkerConfiguration> {

    private ExtensionManager extensionManager;
    private StreamManager streamManager;
    private EventService eventService;
    private Logger logger;

    public Worker() {
        logger = LoggerFactory.getLogger("Worker");

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
            logger.error("Loading plugins ...");
            extensionManager = new ExtensionManager(configuration.getConfigurationStore(), configuration.getEventStore());
            extensionManager.start();
            logger.error("... plugins loaded");
            ;
            logger.error("Starting stream manager ...");
            streamManager = new StreamManager(extensionManager, configuration.getStreamPort());
            streamManager.start();
            logger.error("... stream manager started");

            logger.error("Register event service ...");
            eventService = new EventService(streamManager, configuration.getTimeoutInSeconds());
            environment.jersey().register(eventService);
            eventService.start();
            logger.error("... event service registered");

            logger.error("Register configuration service ...");
            final ConfigurationService configurationService = new ConfigurationService(streamManager);
            environment.jersey().register(configurationService);
            logger.error("... event configuration registered");

        } catch (Exception error) {
            logger.error("Cannot start worker!!!", error);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        logger.error("Stopping stream manager ...");
        if (streamManager != null) {
            streamManager.stop();
        }
        logger.error("... stream manager stopped");

        eventService.stop();

        logger.error("Unloading plugins ...");
        if (extensionManager != null) {
            extensionManager.stop();
        }
        logger.error("... plugins unloaded");
    }
}