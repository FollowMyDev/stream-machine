package stream.machine.core.cluster;

import org.junit.Test;
import stream.machine.core.stream.StreamManager;

public class ClusterManagerTest {


    @Test
    public void testStart() throws Exception {
        StreamManager manager1 = new StreamManager(null,"[\"akka.tcp://StreamManager@localhost:2551\",\"akka.tcp://StreamManager@localhost:2552\"]","localhost",2551);
        manager1.start();

        StreamManager manager2 = new StreamManager(null,"[\"akka.tcp://StreamManager@localhost:2551\",\"akka.tcp://StreamManager@localhost:2552\"]","localhost",2552);

        manager2.start();

        StreamManager manager3 = new StreamManager(null,"[\"akka.tcp://StreamManager@localhost:2551\",\"akka.tcp://StreamManager@localhost:2552\"]","localhost",2553);

        manager3.start();

        manager2.stop();





    }
}