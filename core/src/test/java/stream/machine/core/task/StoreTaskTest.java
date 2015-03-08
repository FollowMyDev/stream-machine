package stream.machine.core.task;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.configuration.EventStorageConfigurationTest;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class StoreTaskTest {

    @Test
    public void testProcess() throws Exception {

        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager, null, "127.0.0.1", 0, 2);

        try {
            streamManager.start();
            Task task = streamManager.getTaskFactory().build("Task");
            task.start();
            DateTime now = new DateTime();

            Event eventA = new Event("TestName", "TypeA");
            eventA.put("a", 12);

            Event eventB = new Event("TestName", "TypeB");
            eventB.put("b", 17);

            ListenableFuture<Event> futureA = task.process(eventA);
            Event resultA = futureA.get(2, TimeUnit.SECONDS);

            ListenableFuture<Event> futureB = task.process(eventB);
            Event resultB = futureB.get(2, TimeUnit.SECONDS);

            List<Event> eventsOfTypeA = storeManager.getEventStore().fetch("TypeA", now.minusHours(1), now.plusHours(1));
            Assert.assertNotNull(eventsOfTypeA);
            Assert.assertEquals(1, eventsOfTypeA.size());
            Assert.assertEquals(12, eventsOfTypeA.get(0).get("a"));
            List<Event> eventsOfTypeB = storeManager.getEventStore().fetch("TypeB", now.minusHours(1), now.plusHours(1));
            Assert.assertNotNull(eventsOfTypeB);
            Assert.assertEquals(1, eventsOfTypeB.size());
            Assert.assertEquals(17, eventsOfTypeB.get(0).get("b"));

            task.stop();
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        } finally {
            streamManager.stop();
        }
    }

    @Test
    public void testProcessMultiple() throws Exception {

        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager, null, "127.0.0.1", 0, 2);
        try {
            streamManager.start();
            Task task = streamManager.getTaskFactory().build("Task");
            task.start();
            ImmutableList.Builder<Event> builder = new ImmutableList.Builder<Event>();
            for (int index = 0; index < 1000; index++) {
                Event eventA = new Event("TestName", "TypeA");
                eventA.put("a", 12);
                builder.add(eventA);
                Event eventB = new Event("TestName", "TypeB");
                eventB.put("b", 17);
                builder.add(eventB);
            }
            ListenableFuture<List<Event>> future = task.processMultiple(builder.build());
            List<Event> result = future.get(2, TimeUnit.SECONDS);
            Assert.assertEquals(2000, result.size());
            task.stop();
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        } finally {
            streamManager.stop();
        }
    }
}