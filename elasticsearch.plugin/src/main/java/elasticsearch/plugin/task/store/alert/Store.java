package elasticsearch.plugin.task.store.alert;

import ro.fortsoft.pf4j.Extension;
import stream.machine.core.model.Alert;
import stream.machine.core.task.store.AlertStore;
import stream.machine.core.task.store.ConfigurationStore;

/**
 * Created by Stephane on 08/01/2015.
 */
@Extension
public class Store implements AlertStore {
    @Override
    public Alert save(Alert alert) {
        return null;
    }

    @Override
    public void open() {


    }

    @Override
    public void close() {

    }
}
