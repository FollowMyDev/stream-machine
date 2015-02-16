package elasticsearch.plugin.task.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.Store;

/**
 * Created by Stephane on 11/01/2015.
 */
public abstract class StoreBase extends ManageableBase implements Store {

    private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
    private final ObjectMapper mapper;
    protected final StoreManager storeManager;
    protected final Logger logger;

    public StoreBase(String name, StoreManager storeManager) {
        super(name);
        this.mapper = new ObjectMapper();
        this.storeManager = storeManager;
        this.logger = LoggerFactory.getLogger(name);
    }

    protected void buildIndex(String index) throws ApplicationException {
        if (!storeManager.isIndexExist(index)) {
            storeManager.createIndex(index);
        }
    }
}
