package elasticsearch.plugin.task.store.configuration;

import elasticsearch.plugin.ElasticsearchPlugin;
import elasticsearch.plugin.task.store.StoreBase;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TaskManagerConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.store.ConfigurationStore;

import java.util.List;

/**
 * Created by Stephane on 08/01/2015.
 */

@Extension
public class Store extends StoreBase implements ConfigurationStore {

    public Store(String name) {
        super(name,"confifuration", ElasticsearchPlugin.getStoreManager());
    }


    @Override
    public List<TaskConfiguration> readAllTask() {
        return null;
    }

    @Override
    public List<TaskManagerConfiguration> readAllTaskManager() {
        return null;
    }

    @Override
    public TaskConfiguration readTask(String taskName) {
        return null;
    }

    @Override
    public TaskManagerConfiguration readTaskManager(String taskName) {
        return null;
    }

    @Override
    public void saveTask(TaskConfiguration configuration) throws ApplicationException {

    }

    @Override
    public void saveTaskManager(TaskManagerConfiguration configuration) throws ApplicationException {

    }

    @Override
    public void updateTask(TaskConfiguration configuration) throws ApplicationException {

    }

    @Override
    public void updateTaskManager(TaskManagerConfiguration configuration) throws ApplicationException {

    }

    @Override
    public void deleteTask(String taskName) throws ApplicationException {

    }

    @Override
    public void deleteTaskManager(String taskManagerName) throws ApplicationException {

    }
}
