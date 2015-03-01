package stream.machine.core.extension;

import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.store.Store;

public class ExtensionManagerTest {

    @Test
    public void testGetConfigurationStore() throws Exception {
        ExtensionManager manager = new ExtensionManager("stream.machine.core.store.memory.MemoryConfigurationStore", "stream.machine.core.store.memory.MemoryEventStore");
        Store store = null;
        try {
            manager.start();
            store = manager.getEventStore();
        } catch (ApplicationException error) {
            Assert.fail(error.getMessage());
        }
        finally {
            manager.stop();
        }
        Assert.assertNotNull(store);
    }
}