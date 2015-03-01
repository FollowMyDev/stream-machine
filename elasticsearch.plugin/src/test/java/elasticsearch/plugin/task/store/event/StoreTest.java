package elasticsearch.plugin.task.store.event;

import elasticsearch.plugin.ElasticsearchTestBase;
import elasticsearch.plugin.StoreConfiguration;
import elasticsearch.plugin.task.store.StoreManager;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class StoreTest extends ElasticsearchTestBase {

    @Test
    public void testSave() throws Exception {
        String indexPattern= "index";

        StoreManager storeManager = new StoreManager(getClient());

        try {
            storeManager.start();
            doTestSave(true, true, indexPattern, storeManager);
            doTestSave(false, true, indexPattern, storeManager);
            doTestSave(true, false, indexPattern, storeManager);
            doTestSave(false, false, indexPattern, storeManager);
        }
        finally {
            storeManager.stop();
        }
    }

    private void doTestSave(boolean useDateInIndex, boolean useTypeIndex, String indexPattern, StoreManager storeManager) throws ApplicationException, InterruptedException {
        EventStore store = new Store(storeManager,useDateInIndex,useTypeIndex,indexPattern);
        store.start();
        DateTime date = new DateTime();
        store.save(new Event("A", "A"));
        store.save(new Event("B", "B"));
        Thread.sleep(2000);
        List<Event> readEvents = store.fetch("A", date.minusDays(1), date.plusDays(1));
        Assert.assertEquals(1, readEvents.size());
        store.stop();
    }

}