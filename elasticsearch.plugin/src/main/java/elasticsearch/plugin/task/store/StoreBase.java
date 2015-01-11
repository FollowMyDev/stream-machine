package elasticsearch.plugin.task.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Client;


import stream.machine.core.exception.ApplicationException;
import stream.machine.core.lifecycle.ManageableBase;
import stream.machine.core.task.store.Store;

/**
 * Created by Stephane on 11/01/2015.
 */
public abstract class StoreBase extends ManageableBase implements Store {

    private final String index;
    private final ObjectMapper mapper;
    private final StoreManager storeManager;

    public StoreBase(String name, String index, StoreManager storeManager) {
        super(name);
        this.index = index;
        this.mapper = new ObjectMapper();
        this.storeManager = storeManager;
    }

    @Override
    public void start() throws ApplicationException {
        if (!storeManager.isIndexExist(this.index)) {
            storeManager.createIndex(this.index);
        }
    }

    @Override
    public void stop() throws ApplicationException{

    }
}
