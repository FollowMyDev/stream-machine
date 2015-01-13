package stream.machine.core.task.store;

import ro.fortsoft.pf4j.ExtensionPoint;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TaskManagerConfiguration;
import stream.machine.core.exception.ApplicationException;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface ConfigurationStore extends Store, ExtensionPoint {

    // TaskConfiguration
    List<TaskConfiguration> readAllTask();

    List<TaskManagerConfiguration> readAllTaskManager();

    TaskConfiguration readTask(String taskName);

    TaskManagerConfiguration readTaskManager(String taskName);

    void saveTask(TaskConfiguration configuration) throws ApplicationException;

    void saveTaskManager(TaskManagerConfiguration configuration) throws ApplicationException;

    void updateTask(TaskConfiguration configuration) throws ApplicationException;

    void updateTaskManager(TaskManagerConfiguration configuration) throws ApplicationException;

    void deleteTask(String taskName) throws ApplicationException;

    void deleteTaskManager(String taskManagerName) throws ApplicationException;
}
