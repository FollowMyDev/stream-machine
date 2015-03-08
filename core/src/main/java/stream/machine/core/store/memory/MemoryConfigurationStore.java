package stream.machine.core.store.memory;

import com.google.common.collect.ImmutableList;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.task.TaskType;

import java.util.List;

/**
 * Created by Stephane on 05/01/2015.
 */
@Extension
public class MemoryConfigurationStore extends ManageableBase implements ConfigurationStore {
    private final MemoryStore<Configuration> configurationStore;

    public MemoryConfigurationStore() {
        super("MemoryConfigurationStore");
        this.configurationStore = new MemoryStore<Configuration>();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    @Override
    public List<Configuration> readAll(TaskType type) throws ApplicationException {
        ImmutableList.Builder<Configuration> builder = new ImmutableList.Builder<Configuration>();
        List<Configuration> configurations = configurationStore.readAll();
        if (configurations != null && configurations.size() > 0) {
            for (Configuration configuration : configurations) {
                if (configuration != null && configuration.getType() == type) {
                    builder.add(configuration);
                }
            }
        }
        return builder.build();
    }

    @Override
    public  Configuration readConfiguration(String name) throws ApplicationException {
        return configurationStore.read(name);
    }

    @Override
    public  void saveConfiguration(Configuration configuration) throws ApplicationException {
        configurationStore.save(configuration.getName(), configuration);
    }

    @Override
    public void updateConfiguration(Configuration configuration) throws ApplicationException {
        configurationStore.update(configuration.getName(), configuration);
    }

    @Override
    public void deleteConfiguration(String name) throws ApplicationException {
        configurationStore.delete(name);
    }
}
