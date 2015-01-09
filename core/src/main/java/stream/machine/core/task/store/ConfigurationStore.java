package stream.machine.core.task.store;

import ro.fortsoft.pf4j.ExtensionPoint;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.exception.ApplicationException;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface ConfigurationStore extends Store,ExtensionPoint {

    // TaskConfiguration
    List<TaskConfiguration> readAll();
    TaskConfiguration read(String taskName);
    void save(TaskConfiguration configuration) throws ApplicationException;
    void update(TaskConfiguration configuration) throws ApplicationException;
    void delete(String taskName) throws ApplicationException;
}
