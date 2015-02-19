package stream.machine.worker.service;

import com.codahale.metrics.annotation.Timed;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.configuration.service.ServiceConfiguration;
import stream.machine.core.configuration.task.EventTransformerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.store.StoreManager;
import stream.machine.core.stream.StreamManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Stephane on 31/01/2015.
 */
@Path("/configuration")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationService {
    private final ConfigurationStore configurationStore;

    public ConfigurationService(StreamManager streamManager) {
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
            return this.configurationStore.readAll(ConfigurationType.EventTransformer, EventTransformerConfiguration.class);
        }
        return null;
    }

    @GET
    @Timed
    @Path("eventTransformer/read")
    EventTransformerConfiguration readConfigurationEventTransformerConfiguration(String name) {
        if (this.configurationStore != null) {
            return this.configurationStore.readConfiguration(name, ConfigurationType.EventTransformer, EventTransformerConfiguration.class);
        }
        return null;
    }

    @PUT
    @Timed
    @Path("eventTransformer/save")
    public void saveConfiguration(EventTransformerConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.saveConfiguration(configuration);
        }
    }

    @PUT
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
    @Path("service/readAll")
    public List<ServiceConfiguration> readAllServiceConfiguration() {
        if (this.configurationStore != null) {
            return this.configurationStore.readAll(ConfigurationType.Service, ServiceConfiguration.class);
        }
        return null;
    }

    @GET
    @Timed
    @Path("service/read")
    ServiceConfiguration readConfigurationServiceConfiguration(String name) {
        if (this.configurationStore != null) {
            return this.configurationStore.readConfiguration(name, ConfigurationType.Service, ServiceConfiguration.class);
        }
        return null;
    }

    @PUT
    @Timed
    @Path("service/save")
    public void saveConfiguration(ServiceConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.saveConfiguration(configuration);
        }
    }

    @PUT
    @Timed
    @Path("eventTransformer/update")
    public void updateConfiguration(ServiceConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.updateConfiguration(configuration);
        }
    }

    @DELETE
    @Timed
    @Path("service/delete")
    public void deleteConfiguration(ServiceConfiguration configuration) throws ApplicationException {
        if (this.configurationStore != null) {
            this.configurationStore.deleteConfiguration(configuration);
        }
    }

}
