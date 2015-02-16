package stream.machine.core.store;

import ro.fortsoft.pf4j.ExtensionPoint;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.exception.ApplicationException;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface ConfigurationStore extends Store, ExtensionPoint {
    <T extends Configuration> List<T> readAll(ConfigurationType type, Class<T> configurationClass);

    <T extends Configuration> T readConfiguration(String name, ConfigurationType type, Class<T> configurationClass);

    <T extends Configuration> void saveConfiguration(T configuration) throws ApplicationException;

    <T extends Configuration> void updateConfiguration(T configuration) throws ApplicationException;

    <T extends Configuration> void deleteConfiguration(T configuration) throws ApplicationException;
}
