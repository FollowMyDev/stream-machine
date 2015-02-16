package stream.machine.core.cluster;

import org.junit.Test;
import stream.machine.core.stream.StreamManager;

public class ClusterManagerTest {


    @Test
    public void testStart() throws Exception {
        StreamManager manager1 = new StreamManager(null,2551);

        manager1.start();

        StreamManager manager2 = new StreamManager(null,2552);

        manager2.start();

        StreamManager manager3 = new StreamManager(null,2553);

        manager3.start();

        manager2.stop();





    }
}