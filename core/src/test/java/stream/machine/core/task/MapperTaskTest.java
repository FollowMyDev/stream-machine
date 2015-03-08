package stream.machine.core.task;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.configuration.MapperConfigurationTest;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapperTaskTest {

    private static Event getEvent() {
        Event event = new Event();
        event.put("a", 1);
        event.put("b", 17);
        event.put("d", "12");
        event.put("e", "true");
        event.put("f", 6);
        event.put("g", true);
        return event;
    }


    @Test
    public void testProcess() throws Exception {

        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(MapperConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager, null, "127.0.0.1", 0, 2);

        try {
            streamManager.start();
            Task task = streamManager.getTaskFactory().build("Task");
            task.start();
            Event event = getEvent();

            ListenableFuture<Event> future = task.process(event);
            Event result = future.get(2, TimeUnit.SECONDS);

            Assert.assertEquals(event.get("d"),result.get("h"));
            Assert.assertEquals(event.get("e"), result.get("i"));
            Assert.assertEquals(event.get("f"), result.get("j"));
            Assert.assertEquals(event.get("g"), result.get("k"));
            task.stop();
        } finally {
            streamManager.stop();
        }
    }


    @Test
    public void testProcessMultiple() throws Exception {

        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(MapperConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager, null, "127.0.0.1", 0, 2);
        try {
            streamManager.start();
            Task task = streamManager.getTaskFactory().build("Task");
            task.start();
            ImmutableList.Builder<Event> builder = new ImmutableList.Builder<Event>();
            for (int index = 0; index < 1000; index++) {
                builder.add(getEvent());
            }
            List<Event> events = builder.build();
            ListenableFuture<List<Event>> future = task.processMultiple(events);
            List<Event> result = future.get(2, TimeUnit.SECONDS);

            Assert.assertEquals(1000, result.size());
            for (int index = 0; index < 1000; index++) {
                Assert.assertEquals(events.get(index).get("d"), result.get(index).get("h"));
                Assert.assertEquals(events.get(index).get("e"), result.get(index).get("i"));
                Assert.assertEquals(events.get(index).get("f"), result.get(index).get("j"));
                Assert.assertEquals(events.get(index).get("g"), result.get(index).get("k"));
            }

            task.stop();
        } finally {
            streamManager.stop();
        }
    }
}