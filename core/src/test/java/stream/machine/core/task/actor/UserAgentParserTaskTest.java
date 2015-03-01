package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.google.common.collect.ImmutableList;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.TransformerConfigurationTest;
import stream.machine.core.configuration.UserAgentParserConfiguration;
import stream.machine.core.configuration.UserAgentParserConfigurationTest;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskType;

import java.util.List;

import static org.junit.Assert.*;

public class UserAgentParserTaskTest {
    private static ActorSystem system;

    @BeforeClass
    public static void oneTimeSetUp() {
        system = ActorSystem.create("UserAgentParserTaskTest");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        system.shutdown();
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testProcess() throws Exception {

    }

    private static Event getEvent() {
        Event event = new Event();
        event.put("UserAgent", "Mozilla/5.0 (BlackBerry; U; BlackBerry 9720; en-GB) AppleWebKit/534.11+ (KHTML, like Gecko) Version/7.1.0.1083 Mobile Safari/534.11+");
        return event;
    }

    @Test
    public void testProcessMultiple() throws Exception {
        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(UserAgentParserConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager,"[\"akka.tcp://StreamManager@localhost:2551\",\"akka.tcp://StreamManager@localhost:2552\"]", "localhost",2550);
        try {
            streamManager.start();
            Task task= streamManager.getTask("Task");
            task.start();
            ImmutableList.Builder<Event> builder  = new ImmutableList.Builder<Event>();
            for (int index=0; index < 1000; index++)
            {
                builder.add(getEvent());
            }

            Timeout timeout = new Timeout(Duration.create(1000, "seconds"));
            Future<List<Event>> future = task.processMultiple(builder.build());
            List<Event> result = Await.result(future, timeout.duration());

            Assert.assertEquals(1000, result.size());
            for (int index=0; index < 1000; index++)
            {
                Assert.assertEquals("BlackBerry OS", result.get(index).get("userAgentOperatingSystem"));
            }

            task.stop();
        }
        finally {
            streamManager.stop();
        }
    }
}