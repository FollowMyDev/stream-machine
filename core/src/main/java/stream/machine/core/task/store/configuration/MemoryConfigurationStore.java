package stream.machine.core.task.store.configuration;

import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.task.store.ConfigurationStore;
import stream.machine.core.task.store.MemoryStoreBase;

import java.util.List;

/**
 * Created by Stephane on 05/01/2015.
 */
@Extension
public class MemoryConfigurationStore extends ManageableBase implements ConfigurationStore {

    private final MemoryStoreBase<TaskConfiguration> taskConfigurations;

    public MemoryConfigurationStore() {
       super("MemoryConfigurationStore");
        this.taskConfigurations = new MemoryStoreBase<TaskConfiguration>();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public List<TaskConfiguration> readAll() {
        return this.taskConfigurations.readAll();
    }

    @Override
    public TaskConfiguration read(String taskName) {
        return this.taskConfigurations.read(taskName);
    }

    @Override
    public void save(TaskConfiguration configuration) throws ApplicationException {
        if (configuration != null) {
            this.taskConfigurations.save(configuration.getName(), configuration);
        }
    }

    @Override
    public void update(TaskConfiguration configuration) throws ApplicationException {
        if (configuration != null) {
            this.taskConfigurations.update(configuration.getName(), configuration);
        }
    }

    @Override
    public void delete(String taskName) throws ApplicationException {
        this.taskConfigurations.delete(taskName);
    }


}
