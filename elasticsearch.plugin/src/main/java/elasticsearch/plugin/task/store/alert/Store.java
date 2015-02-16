package elasticsearch.plugin.task.store.alert;

import elasticsearch.plugin.ElasticsearchPlugin;
import elasticsearch.plugin.task.store.StoreBase;
import elasticsearch.plugin.task.store.StoreManager;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Alert;
import stream.machine.core.store.AlertStore;

/**
 * Created by Stephane on 08/01/2015.
 */
@Extension
public class Store extends StoreBase implements AlertStore {
    public Store(StoreManager storeManager) {
        super("Elasticsearch.AlertStore",storeManager );
    }

    public Store() {
        super("Elasticsearch.AlertStore",ElasticsearchPlugin.getStoreManager() );
    }

    @Override
    public Alert save(Alert alert) {
        return null;
    }


    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }
}
