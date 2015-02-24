package stream.machine.core.store;

import ro.fortsoft.pf4j.ExtensionPoint;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.TaskType;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface ConfigurationStore extends Store, ExtensionPoint {
    <T extends Configuration> List<T> readAll(TaskType type, Class<T> configurationClass) throws ApplicationException;

    <T extends Configuration> T readConfiguration(String name, TaskType type, Class<T> configurationClass) throws ApplicationException;

    <T extends Configuration> void saveConfiguration(T configuration) throws ApplicationException;

    <T extends Configuration> void updateConfiguration(T configuration) throws ApplicationException;

    <T extends Configuration> void deleteConfiguration(T configuration) throws ApplicationException;
}
