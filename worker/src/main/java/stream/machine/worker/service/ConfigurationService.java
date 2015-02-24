package stream.machine.worker.service;

import com.codahale.metrics.annotation.Timed;
import stream.machine.core.configuration.store.EventStorageConfiguration;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
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

    public ConfigurationService(StreamManager streamManager) {
        super("ConfigurationService");
        if (streamManager != null) {
            StoreManager storeManager = streamManager.getStoreManager();
            if (storeManager != null) {
                this.configurationStore = streamManager.getStoreManager().getConfigurationStore();
            } else {
                this.configurationStore = null;
            }
        } else {
            this.configurationStore = null;
        }
    }

    @GET
    @Timed
    @Path("eventTransformer/readAll")
    public List<EventTransformerConfiguration> readAllEventTransformerConfiguration() {
        if (this.configurationStore != null) {
            try {
                return this.configurationStore.readAll(TaskType.Transform, EventTransformerConfiguration.class);
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        }
        return null;
    }

    @GET
    @Timed
    @Path("eventTransformer/read/{name}")
    public EventTransformerConfiguration readConfigurationEventTransformerConfiguration(@PathParam("name")String name) {
        if (this.configurationStore != null) {
            try {
                return this.configurationStore.readConfiguration(name, TaskType.Transform, EventTransformerConfiguration.class);
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        }
        return null;
    }

    @POST
    @Timed
    @Path("eventTransformer/save")
    public void saveConfiguration(EventTransformerConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.saveConfiguration(configuration);
        }
    }

    @POST
    @Timed
    @Path("eventTransformer/update")
    public void updateConfiguration(EventTransformerConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.updateConfiguration(configuration);
        }
    }

    @DELETE
    @Timed
    @Path("eventTransformer/delete")
    public void deleteConfiguration(EventTransformerConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.deleteConfiguration(configuration);
        }
    }

    @GET
    @Timed
    @Path("store/readAll")
    public List<EventStorageConfiguration> readAllServiceConfiguration() {
        if (this.configurationStore != null) {
            try {
                return this.configurationStore.readAll(TaskType.Store, EventStorageConfiguration.class);
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        }
        return null;
    }

    @GET
    @Timed
    @Path("store/read/{name}")
    public EventStorageConfiguration readConfigurationServiceConfiguration(@PathParam("name") String name) {
        if (this.configurationStore != null) {
            try {
                return this.configurationStore.readConfiguration(name, TaskType.Store, EventStorageConfiguration.class);
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }
        }
        return null;
    }

    @PUT
    @Timed
    @Path("store/save")
    public void saveConfiguration(EventStorageConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.saveConfiguration(configuration);
        }
    }

    @POST
    @Timed
    @Path("store/update")
    public void updateConfiguration(EventStorageConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.updateConfiguration(configuration);
        }
    }

    @DELETE
    @Timed
    @Path("store/delete")
    public void deleteConfiguration(EventStorageConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.deleteConfiguration(configuration);
        }
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }
}
