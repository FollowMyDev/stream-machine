package stream.machine.core.store;

/**
 * Created by Stephane on 15/02/2015.
 */
public interface StoreManager {
    ConfigurationStore getConfigurationStore();
    EventStore getEventStore();
}
