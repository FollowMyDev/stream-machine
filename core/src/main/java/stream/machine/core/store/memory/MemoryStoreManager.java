package stream.machine.core.store.memory;

import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.store.EventStore;
import stream.machine.core.store.StoreManager;

/**
 * Created by Stephane on 21/02/2015.
 */
public class MemoryStoreManager implements StoreManager {

    private final EventStore eventStore;
    private final ConfigurationStore configurationStore;

    public MemoryStoreManager() {
        configurationStore = new MemoryConfigurationStore();
        eventStore = new MemoryEventStore();
    }

    @Override
    public ConfigurationStore getConfigurationStore() {
        return configurationStore;
    }

    @Override
    public EventStore getEventStore() {
        return eventStore;
    }
}
