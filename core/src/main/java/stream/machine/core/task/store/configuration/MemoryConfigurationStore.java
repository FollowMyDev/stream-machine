package stream.machine.core.task.store.configuration;

import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TaskManagerConfiguration;
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
    private final MemoryStoreBase<TaskManagerConfiguration> taskMnagerConfigurations;

    public MemoryConfigurationStore() {
        super("MemoryConfigurationStore");
        this.taskConfigurations = new MemoryStoreBase<TaskConfiguration>();
        this.taskMnagerConfigurations = new MemoryStoreBase<TaskManagerConfiguration>();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public List<TaskConfiguration> readAllTask() {
        return this.taskConfigurations.readAll();
    }

    @Override
    public TaskConfiguration readTask(String taskName) {
        return this.taskConfigurations.read(taskName);
    }

    @Override
    public void saveTask(TaskConfiguration configuration) throws ApplicationException {
        if (configuration != null) {
            this.taskConfigurations.save(configuration.getName(), configuration);
        }
    }

    @Override
    public void updateTask(TaskConfiguration configuration) throws ApplicationException {
        if (configuration != null) {
            this.taskConfigurations.update(configuration.getName(), configuration);
        }
    }

    @Override
    public void deleteTask(String taskName) throws ApplicationException {
        this.taskConfigurations.delete(taskName);
    }

    @Override
    public List<TaskManagerConfiguration> readAllTaskManager() {
        return this.taskMnagerConfigurations.readAll();
    }

    @Override
    public TaskManagerConfiguration readTaskManager(String taskNameManager) {
        return this.taskMnagerConfigurations.read(taskNameManager);
    }

    @Override
    public void saveTaskManager(TaskManagerConfiguration configuration) throws ApplicationException {
        if (configuration != null) {
            this.taskMnagerConfigurations.save(configuration.getName(), configuration);
        }
    }

    @Override
    public void updateTaskManager(TaskManagerConfiguration configuration) throws ApplicationException {
        if (configuration != null) {
            this.taskMnagerConfigurations.update(configuration.getName(), configuration);
        }
    }

    @Override
    public void deleteTaskManager(String taskNameManager) throws ApplicationException {
        this.taskMnagerConfigurations.delete(taskNameManager);
    }


}
