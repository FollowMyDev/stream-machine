package stream.machine.core.store.memory;

import ro.fortsoft.pf4j.Extension;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Alert;
import stream.machine.core.store.AlertStore;
import stream.machine.core.store.EventStore;

/**
 * Created by Stephane on 28/02/2015.
 */
@Extension
public class MemoryAlertStore extends ManageableBase implements AlertStore {

    protected MemoryAlertStore() {
        super("MemoryAlertStore");
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
