package stream.machine.core.store.memory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.ConfigurationStore;

import java.util.List;

/**
 * Created by Stephane on 05/01/2015.
 */
@Extension
public class MemoryConfigurationStore extends ManageableBase implements ConfigurationStore {
    private final MemoryStore<Configuration> configurations;

    public MemoryConfigurationStore() {
        super("MemoryConfigurationStore");
        this.configurations = new MemoryStore<Configuration>();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    @Override
    public <T extends Configuration> List<T> readAll(ConfigurationType type, Class<T> configurationClass) {
        List<Configuration> items = configurations.readAll();
        if (items != null && items.size() > 0) {
            Function<Configuration, T> convertTo = new Function<Configuration, T>() {
                @Override
                public T apply(Configuration item) {
                    return (T) item;
                }
            };
            return ImmutableList.copyOf(Lists.transform(items, convertTo));
        }
        return null;
    }

    @Override
    public <T extends Configuration> T readConfiguration(String name,ConfigurationType type,Class<T> configurationClass) {
        Configuration item = configurations.read(name);
        return (T) item;
    }

    @Override
    public <T extends Configuration> void saveConfiguration(T configuration) throws ApplicationException {
        configurations.save(configuration.getName(),configuration);
    }

    @Override
    public <T extends Configuration> void updateConfiguration(T configuration) throws ApplicationException {
        configurations.update(configuration.getName(), configuration);
    }

    @Override
    public <T extends Configuration> void deleteConfiguration(T configuration) throws ApplicationException {
        configurations.delete(configuration.getName());
    }
}
