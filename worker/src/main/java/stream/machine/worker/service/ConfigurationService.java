package stream.machine.worker.service;

import stream.machine.core.store.ConfigurationStore;

/**
 * Created by Stephane on 31/01/2015.
 */
public class ConfigurationService {
    private final ConfigurationStore configurationStore;

    public ConfigurationService(ConfigurationStore configurationStore) {
        this.configurationStore = configurationStore;
    }


}
