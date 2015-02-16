package elasticsearch.plugin.task.store.configuration;

import elasticsearch.plugin.ElasticsearchTestBase;
import elasticsearch.plugin.task.store.StoreManager;
import junit.framework.Assert;
import org.junit.Test;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.configuration.service.ServiceConfiguration;
import stream.machine.core.configuration.task.EventTransformerConfiguration;
import stream.machine.core.store.ConfigurationStore;

import java.util.List;

public class StoreTest extends ElasticsearchTestBase {

    @Test
    public void testReadAll() throws Exception {
        StoreManager storeManager = new StoreManager(getClient());

        try {
            storeManager.start();
            ConfigurationStore store = new Store(storeManager);
            store.start();

            EventTransformerConfiguration eventTransformerConfiguration = new EventTransformerConfiguration("T", "");
            ServiceConfiguration serviceConfiguration = new ServiceConfiguration("S", 0);
            store.saveConfiguration(eventTransformerConfiguration);
            store.saveConfiguration(serviceConfiguration);
            Thread.sleep(5000);
            List<EventTransformerConfiguration> eventTransformerConfigurations = store.readAll(ConfigurationType.EventTransformer, EventTransformerConfiguration.class);
            Assert.assertEquals(1, eventTransformerConfigurations.size());
            List<ServiceConfiguration> serviceConfigurations = store.readAll(ConfigurationType.Service, ServiceConfiguration.class);
            Assert.assertEquals(1, serviceConfigurations.size());
            store.stop();
        } finally {
            storeManager.stop();
        }
    }


    @Test
    public void testReadConfiguration() throws Exception {
        StoreManager storeManager = new StoreManager(getClient());

        try {
            storeManager.start();
            ConfigurationStore store = new Store(storeManager);
            store.start();
            ServiceConfiguration writeConfiguration = new ServiceConfiguration("S", 0);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(5000);
            ServiceConfiguration readConfiguration = store.readConfiguration("S", ConfigurationType.Service, ServiceConfiguration.class);
            Assert.assertEquals(writeConfiguration.getType(), readConfiguration.getType());
            store.stop();
        } finally {
            storeManager.stop();
        }
    }



    @Test
    public void testUpdateConfiguration() throws Exception {
        StoreManager storeManager = new StoreManager(getClient());

        try {
            storeManager.start();
            ConfigurationStore store = new Store(storeManager);
            store.start();
            ServiceConfiguration writeConfiguration = new ServiceConfiguration("S", 0);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(5000);

            ServiceConfiguration readConfiguration = store.readConfiguration("S", ConfigurationType.Service, ServiceConfiguration.class);
            Assert.assertEquals(0,readConfiguration.getTimeOutInMilliseconds());

            writeConfiguration = new ServiceConfiguration("S", 5);
            store.updateConfiguration(writeConfiguration);

            Thread.sleep(5000);
            readConfiguration = store.readConfiguration("S", ConfigurationType.Service, ServiceConfiguration.class);
            Assert.assertEquals(5,readConfiguration.getTimeOutInMilliseconds());

            store.stop();
        } finally {
            storeManager.stop();
        }
    }

    @Test
    public void testDeleteConfiguration() throws Exception {
        StoreManager storeManager = new StoreManager(getClient());

        try {
            storeManager.start();
            ConfigurationStore store = new Store(storeManager);
            store.start();
            ServiceConfiguration writeConfiguration = new ServiceConfiguration("S", 0);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(5000);
            ServiceConfiguration readConfiguration = store.readConfiguration("S", ConfigurationType.Service, ServiceConfiguration.class);
            Assert.assertEquals(writeConfiguration.getType(), readConfiguration.getType());

            store.deleteConfiguration(writeConfiguration);
            readConfiguration = store.readConfiguration("S", ConfigurationType.Service, ServiceConfiguration.class);
            Assert.assertNull(readConfiguration);

            store.stop();
        } finally {
            storeManager.stop();
        }
    }
}