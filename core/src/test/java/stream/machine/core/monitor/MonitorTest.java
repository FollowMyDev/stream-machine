package stream.machine.core.monitor;

import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.TaskType;

public class MonitorTest {


    private class MockConsumer implements MonitorConsumer {
        private Configuration configuration;
        private final String name;

        private MockConsumer(String name) {
            this.name = name;
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public void setConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void onMessage(Message message) {
            if (message != null) {
                if (message instanceof ConfigurationMessage) {
                    ConfigurationMessage configurationMessage = (ConfigurationMessage) message;
                    this.configuration = configurationMessage.getConfiguration();
                }

            }
        }

        public void reset() {
            configuration = null;
        }

    }

//    @Test
//    public void testSend() throws Exception {
//        StreamManager manager1 = new StreamManager(null,"[\"akka.tcp://StreamManager@127.0.0.1:2551\"]","127.0.0.1", 2550);
//        MockConsumer consumer1 = new MockConsumer("consumer1");
//        manager1.start();
//        manager1.register("Create", consumer1);
//        manager1.register("Update", consumer1);
//
//        StreamManager manager2 = new StreamManager(null, "[\"akka.tcp://StreamManager@127.0.0.1:2551\",\"akka.tcp://StreamManager@127.0.0.1:2552\"]","127.0.0.1",2552);
//        manager2.start();
//        MockConsumer consumer2 = new MockConsumer("consumer2");
//        manager2.register("Create", consumer2);
//        manager2.register("Delete", consumer2);
//
//        StreamManager manager3 = new StreamManager(null,"[\"akka.tcp://StreamManager@127.0.0.1:2551\",\"akka.tcp://StreamManager@127.0.0.1:2552\"]","127.0.0.1", 2553);
//        manager3.start();
//        MockConsumer consumer3 = new MockConsumer("consumer3");
//        manager3.register("Delete", consumer3);
//        manager3.register("Update", consumer3);
//
//        Thread.sleep(5000);
//
//        manager1.send(new ConfigurationMessage("Create",new Configuration("Create", TaskType.Map)));
//
//        Assert.assertEquals("Create",consumer1.getConfiguration().getName());
//        Assert.assertEquals("Create",consumer2.getConfiguration().getName());
//        Assert.assertNull(consumer3);
//
//    }
}