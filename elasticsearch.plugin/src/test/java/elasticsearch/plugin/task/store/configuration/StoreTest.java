package elasticsearch.plugin.task.store.configuration;

import elasticsearch.plugin.ElasticsearchTestBase;
import elasticsearch.plugin.task.store.StoreManager;
import junit.framework.Assert;
import org.junit.Test;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.configuration.store.EventStorageConfiguration;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
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
            EventStorageConfiguration serviceConfiguration = new EventStorageConfiguration("S", 100, 1000,1000);
            store.saveConfiguration(eventTransformerConfiguration);
            store.saveConfiguration(serviceConfiguration);
            Thread.sleep(5000);
            List<EventTransformerConfiguration> eventTransformerConfigurations = store.readAll(ConfigurationType.Transform, EventTransformerConfiguration.class);
            Assert.assertEquals(1, eventTransformerConfigurations.size());
            List<EventStorageConfiguration> serviceConfigurations = store.readAll(ConfigurationType.Store, EventStorageConfiguration.class);
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
            EventStorageConfiguration writeConfiguration = new EventStorageConfiguration("S",1000,1000,1000);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(5000);
            EventStorageConfiguration readConfiguration = store.readConfiguration("S", ConfigurationType.Store, EventStorageConfiguration.class);
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
            EventStorageConfiguration writeConfiguration = new EventStorageConfiguration("S", 1000,1000,1000);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(5000);

            EventStorageConfiguration readConfiguration = store.readConfiguration("S", ConfigurationType.Store, EventStorageConfiguration.class);
            Assert.assertEquals(1000,readConfiguration.getTimeOutInMilliseconds());

            writeConfiguration = new EventStorageConfiguration("S",5000,1000,1000);
            store.updateConfiguration(writeConfiguration);

            Thread.sleep(5000);
            readConfiguration = store.readConfiguration("S", ConfigurationType.Store, EventStorageConfiguration.class);
            Assert.assertEquals(5000,readConfiguration.getTimeOutInMilliseconds());

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
            EventStorageConfiguration writeConfiguration = new EventStorageConfiguration("S", 1000,1000,1000);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(5000);
            EventStorageConfiguration readConfiguration = store.readConfiguration("S", ConfigurationType.Store, EventStorageConfiguration.class);
            Assert.assertEquals(writeConfiguration.getType(), readConfiguration.getType());

            store.deleteConfiguration(writeConfiguration);
            readConfiguration = store.readConfiguration("S", ConfigurationType.Store, EventStorageConfiguration.class);
            Assert.assertNull(readConfiguration);

            store.stop();
        } finally {
            storeManager.stop();
        }
    }
}