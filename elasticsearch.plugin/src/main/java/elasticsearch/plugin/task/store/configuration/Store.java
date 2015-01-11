package elasticsearch.plugin.task.store.configuration;

import elasticsearch.plugin.ElasticsearchPlugin;
import elasticsearch.plugin.task.store.StoreBase;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.task.TaskConfiguration;
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
    public List<TaskConfiguration> readAll() {
        return null;
    }

    @Override
    public TaskConfiguration read(String taskName) {
        return null;
    }

    @Override
    public void save(TaskConfiguration configuration) throws ApplicationException {

    }

    @Override
    public void update(TaskConfiguration configuration) throws ApplicationException {

    }

    @Override
    public void delete(String taskName) throws ApplicationException {

    }

}
