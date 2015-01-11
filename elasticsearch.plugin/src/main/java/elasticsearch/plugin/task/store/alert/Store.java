package elasticsearch.plugin.task.store.alert;

import elasticsearch.plugin.ElasticsearchPlugin;
import elasticsearch.plugin.task.store.StoreBase;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.model.Alert;
import stream.machine.core.task.store.AlertStore;
import stream.machine.core.task.store.ConfigurationStore;

/**
 * Created by Stephane on 08/01/2015.
 */
@Extension
public class Store extends StoreBase implements AlertStore {
    public Store(String name) {
        super(name,"alerts", ElasticsearchPlugin.getStoreManager());
    }

    @Override
    public Alert save(Alert alert) {
        return null;
    }


}
