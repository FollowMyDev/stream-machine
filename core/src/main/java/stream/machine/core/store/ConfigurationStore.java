package stream.machine.core.store;

import ro.fortsoft.pf4j.ExtensionPoint;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.TaskType;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface ConfigurationStore extends Store {
    List<Configuration> readAll(TaskType type) throws ApplicationException;

    Configuration readConfiguration(String name) throws ApplicationException;

    void saveConfiguration(Configuration configuration) throws ApplicationException;

    void updateConfiguration(Configuration configuration) throws ApplicationException;

    void deleteConfiguration(Configuration configuration) throws ApplicationException;
}
