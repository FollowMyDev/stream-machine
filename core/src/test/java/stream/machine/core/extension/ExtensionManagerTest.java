package stream.machine.core.extension;

import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.store.ConfigurationStore;

public class ExtensionManagerTest {

    @Test
    public void testGetConfigurationStore() throws Exception {
        ExtensionManager manager = new ExtensionManager();
        ConfigurationStore store = null;
        try {
            manager.start();
            store = manager.getConfigurationStore("stream.machine.core.task.store.configuration.MemoryConfigurationStore");
        } catch (ApplicationException error) {
            Assert.fail(error.getMessage());
        }
        finally {
            manager.stop();
        }
        Assert.assertNotNull(store);
    }
}