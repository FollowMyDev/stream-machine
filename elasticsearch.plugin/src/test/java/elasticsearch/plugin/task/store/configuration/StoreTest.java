package elasticsearch.plugin.task.store.configuration;

import elasticsearch.plugin.ElasticsearchTestBase;
import elasticsearch.plugin.task.store.StoreManager;
import junit.framework.Assert;
import org.junit.Test;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.StorageConfiguration;
import stream.machine.core.configuration.TransformerConfiguration;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.task.TaskType;

import java.util.List;

public class StoreTest extends ElasticsearchTestBase {

    @Test
    public void testReadAll() throws Exception {
        StoreManager storeManager = new StoreManager(getClient());

        try {
            storeManager.start();
            ConfigurationStore store = new Store(storeManager);
            store.start();

            TransformerConfiguration transformerConfiguration = new TransformerConfiguration("T", "");
            StorageConfiguration serviceConfiguration = new StorageConfiguration("S", 100, 1000,1000);
            store.saveConfiguration(transformerConfiguration);
            store.saveConfiguration(serviceConfiguration);
            Thread.sleep(2000);
            List<Configuration> transformerConfigurations = store.readAll(TaskType.Transform);
            Assert.assertEquals(1, transformerConfigurations.size());
            List<Configuration> serviceConfigurations = store.readAll(TaskType.Store);
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
            StorageConfiguration writeConfiguration = new StorageConfiguration("S",1000,1000,1000);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(2000);
            Configuration readConfiguration = store.readConfiguration("S");
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
            StorageConfiguration writeConfiguration = new StorageConfiguration("S", 1000,1000,1000);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(2000);

            StorageConfiguration readConfiguration = new StorageConfiguration(store.readConfiguration("S"));
            Assert.assertEquals(1000,readConfiguration.getTimeOutInMilliseconds());

            writeConfiguration = new StorageConfiguration("S",5000,1000,1000);
            store.updateConfiguration(writeConfiguration);

            Thread.sleep(2000);
            readConfiguration = new StorageConfiguration(store.readConfiguration("S"));
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
            StorageConfiguration writeConfiguration = new StorageConfiguration("S", 1000,1000,1000);
            store.saveConfiguration(writeConfiguration);
            Thread.sleep(2000);
            Configuration readConfiguration = new StorageConfiguration(store.readConfiguration("S"));
            Assert.assertEquals(writeConfiguration.getType(), readConfiguration.getType());

            store.deleteConfiguration(writeConfiguration.getName());
            readConfiguration = store.readConfiguration("S");
            Assert.assertNull(readConfiguration);

            store.stop();
        } finally {
            storeManager.stop();
        }
    }
}