package stream.machine.core.task;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.configuration.EventStorageConfigurationTest;
import stream.machine.core.configuration.SequenceConfigurationTest;
import stream.machine.core.configuration.TransformerConfigurationTest;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SequenceTaskTest {

    @Test
    public void testProcess() throws Exception {
        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(TransformerConfigurationTest.build("TaskA"));
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("TaskB"));
        storeManager.getConfigurationStore().saveConfiguration(SequenceConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager, null, "127.0.0.1", 0, 2);
        try {
            DateTime now = new DateTime();
            streamManager.start();
            Task task = streamManager.getTaskFactory().build("Task");
            task.start();
            Event eventA = new Event("TestName", "TypeA");
            eventA.put("a", 12);
            eventA.put("b", 11);
            ListenableFuture<Event> future = task.process(eventA);
            Event resultA = future.get(2, TimeUnit.SECONDS);
            Assert.assertTrue(!resultA.containsKey(task.getErrorField()));
            List<Event> eventsOfTypeA = storeManager.getEventStore().fetch("TypeA", now.minusHours(1), now.plusHours(1));
            Assert.assertNotNull(eventsOfTypeA);
            Assert.assertEquals(1, eventsOfTypeA.size());
            Assert.assertEquals(23, eventsOfTypeA.get(0).get("c"));
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
        storeManager.getConfigurationStore().saveConfiguration(TransformerConfigurationTest.build("TaskA"));
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("TaskB"));
        storeManager.getConfigurationStore().saveConfiguration(SequenceConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager, null, "127.0.0.1", 0, 2);
        try {
            DateTime now = new DateTime();
            streamManager.start();
            Task task = streamManager.getTaskFactory().build("Task");
            task.start();
            ImmutableList.Builder<Event> builder = new ImmutableList.Builder<Event>();
            for (int index = 0; index < 1000; index++) {
                Event eventA = new Event("TestName", "TypeA");
                eventA.put("a", 12);
                eventA.put("b", 11);
                builder.add(eventA);
            }
            ListenableFuture<List<Event>> future = task.processMultiple(builder.build());
            List<Event> result = future.get(2, TimeUnit.SECONDS);
            Assert.assertEquals(1000, result.size());


            List<Event> eventsOfTypeA = storeManager.getEventStore().fetch("TypeA", now.minusHours(1), now.plusHours(1));
            Assert.assertNotNull(eventsOfTypeA);
            Assert.assertEquals(1000, eventsOfTypeA.size());

            for (int index = 0; index < 1000; index++) {
                Assert.assertEquals(23, eventsOfTypeA.get(index).get("c"));
            }

            task.stop();
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        } finally {
            streamManager.stop();
        }
    }
}