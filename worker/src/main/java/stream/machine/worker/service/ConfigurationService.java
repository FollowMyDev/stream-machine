package stream.machine.worker.service;

import com.codahale.metrics.annotation.Timed;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.StorageConfiguration;
import stream.machine.core.configuration.TransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.monitor.ConfigurationMessage;
import stream.machine.core.monitor.Message;
import stream.machine.core.monitor.MonitorProducer;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.store.StoreManager;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.TaskType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Stephane on 31/01/2015.
 */
@Path("/configuration")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationService extends ManageableBase{
    private final ConfigurationStore configurationStore;
    private final MonitorProducer monitorProducer;

    public ConfigurationService(StreamManager streamManager) {
        super("ConfigurationService");
        if (streamManager != null) {
            this.monitorProducer = streamManager;
            StoreManager storeManager = streamManager.getStoreManager();
            if (storeManager != null) {
                this.configurationStore = streamManager.getStoreManager().getConfigurationStore();
            } else {
                this.configurationStore = null;
            }
        } else {
            this.configurationStore = null;
            this.monitorProducer = null;
        }
    }

    @GET
    @Timed
    @Path("readAll/{type}")
    public List<Configuration> readAllEventTransformerConfiguration(@PathParam("type") String type) {
        if (this.configurationStore != null) {
            try {
                return this.configurationStore.readAll(TaskType.valueOf(type));
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        }
        return null;
    }

    @GET
    @Timed
    @Path("read/{name}")
    public Configuration readConfigurationEventTransformerConfiguration(@PathParam("name")String name) {
        if (this.configurationStore != null) {
            try {
                return this.configurationStore.readConfiguration(name);
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        }
        return null;
    }

    @POST
    @Timed
    @Path("save")
    public void saveConfiguration(Configuration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.saveConfiguration(configuration);
            if (this.monitorProducer != null) {
                Message configurationMessage = new ConfigurationMessage(ServiceTopics.TaskConfigurationCreated,configuration);
                monitorProducer.send(configurationMessage);
            }
        }
    }

    @POST
    @Timed
    @Path("update")
    public void updateConfiguration(Configuration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.updateConfiguration(configuration);
            if (this.monitorProducer != null) {
                Message configurationMessage = new ConfigurationMessage(ServiceTopics.TaskConfigurationUpdated,configuration);
                monitorProducer.send(configurationMessage);
            }
        }
    }

    @DELETE
    @Timed
    @Path("delete")
    public void deleteConfiguration(Configuration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.deleteConfiguration(configuration);
            if (this.monitorProducer != null) {
                Message configurationMessage = new ConfigurationMessage(ServiceTopics.TaskConfigurationDeleted,configuration);
                monitorProducer.send(configurationMessage);
            }
        }
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }
}
