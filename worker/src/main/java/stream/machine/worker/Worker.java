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
    private ConfigurationService configurationService;
    private Logger logger;

    public Worker() {
        logger = LoggerFactory.getLogger("Task");

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
            logger.info("Loading plugins ...");
            extensionManager = new ExtensionManager(configuration.getConfigurationStore(), configuration.getEventStore());
            extensionManager.start();
            logger.info("... plugins loaded");
            ;
            logger.info("Starting stream manager ...");
            streamManager = new StreamManager(extensionManager, configuration.getMembers(),configuration.getHostname(),configuration.getStreamPort(), configuration.getConcurrency());
            streamManager.start();
            logger.info("... stream manager started");

            logger.info("Register event store ...");
            eventService = new EventService(streamManager, configuration.getTimeoutInSeconds());
            eventService.start();
            environment.jersey().register(eventService);
            logger.info("... event store registered");

            logger.info("Register configuration store ...");
            configurationService = new ConfigurationService(streamManager);
            configurationService.start();
            environment.jersey().register(configurationService);
            logger.info("... event configuration registered");

        } catch (Exception error) {
            logger.error("Cannot start worker!!!", error);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        logger.info("Stopping stream manager ...");
        if (streamManager != null) {
            streamManager.stop();
        }
        logger.info("... stream manager stopped");

        logger.info("Removing configuration store ...");
        configurationService.stop();
        logger.info("... event configuration removed");

        logger.info("Removing event store ...");
        eventService.stop();
        logger.info("... event store removed");

        logger.info("Unloading plugins ...");
        if (extensionManager != null) {
            extensionManager.stop();
        }
        logger.info("... plugins unloaded");
    }
}