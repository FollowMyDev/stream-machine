package stream.machine.core.cluster;

import junit.framework.Assert;
import org.junit.Test;
import stream.machine.core.communication.MessageConsumer;
import stream.machine.core.model.Event;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;

public class StreamManagerTest {

    @Test
    public void testStart() throws Exception {
        StreamManager manager1 = new StreamManager(new MemoryStoreManager(),null,"127.0.0.1",0, 2);
        manager1.start();
        Thread.sleep(2000);
        Assert.assertTrue(manager1.isRunning());
        Assert.assertTrue(manager1.isMaster());

        StreamManager manager2 = new StreamManager(new MemoryStoreManager(),null,"127.0.0.1",0, 2);
        manager2.start();
        Thread.sleep(2000);
        Assert.assertTrue(manager2.isRunning());
        Assert.assertFalse(manager2.isMaster());

        StreamManager manager3 = new StreamManager(new MemoryStoreManager(),null,"127.0.0.1",0, 2);
        manager3.start();
        Thread.sleep(2000);
        Assert.assertTrue(manager3.isRunning());
        Assert.assertFalse(manager3.isMaster());

        manager1.stop();
        Assert.assertFalse(manager1.isRunning());
        Thread.sleep(2000);
        Assert.assertTrue(manager2.isMaster() || manager3.isMaster());

        manager2.stop();
        Thread.sleep(2000);
        Assert.assertFalse(manager2.isRunning());
        Assert.assertTrue(manager3.isMaster());

        manager1.start();
        Thread.sleep(2000);
        Assert.assertTrue(manager1.isRunning());
        Assert.assertFalse(manager1.isMaster());

        manager3.stop();
        Thread.sleep(2000);
        Assert.assertFalse(manager3.isRunning());
        Assert.assertFalse(manager3.isMaster());
        Assert.assertTrue(manager1.isMaster());
    }

    private class MockConsumer implements MessageConsumer {
        private Object data;
        private final String topic;

        private MockConsumer(String topic) {
            this.topic = topic;
        }

        public Object getData() {
            return data;
        }

        public String getTopic() {
            return topic;
        }

        @Override
        public <T> void onMessage(String topic, T data) {
            Assert. assertEquals(this.topic, topic);
            this.data = data;
        }
    }

    @Test
    public void testPublishSubscribe() throws Exception {
        StreamManager manager1 = new StreamManager(new MemoryStoreManager(),null,"127.0.0.1",0, 2);
        manager1.start();

        MockConsumer consumerA = new MockConsumer("TypeA");
        manager1.register("TypeA",consumerA);
        MockConsumer consumerB = new MockConsumer("TypeB");
        manager1.register("TypeB",consumerB);
        Event eventA = new Event("EventA","TypeA");
        manager1.send("TypeA",eventA);

        StreamManager manager2 = new StreamManager(new MemoryStoreManager(),null,"127.0.0.1",0, 2);
        manager2.start();
        Event eventB = new Event("EventB","TypeB");
        manager2.send("TypeB", eventB);

        Thread.sleep(2000);
        Assert.assertEquals(eventA,consumerA.getData());
        Assert.assertEquals(eventB,consumerB.getData());
        manager1.stop();
        manager2.stop();
    }
}