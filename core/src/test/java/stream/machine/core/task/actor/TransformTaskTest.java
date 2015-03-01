package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.google.common.collect.ImmutableList;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.TransformerConfigurationTest;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskType;

import java.util.List;

public class TransformTaskTest {
    private static ActorSystem system;

    @BeforeClass
    public static void oneTimeSetUp() {
        system = ActorSystem.create("TransformTaskTest");
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

        Task transformTask = new TransformTask(TransformerConfigurationTest.build("Simple"), system);

        transformTask.start();
        Event event = getEvent();

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Future<Event> future = transformTask.process(event);
        Event result = Await.result(future, timeout.duration());

        Assert.assertEquals(18, result.get("c"));
        transformTask.stop();
    }

    private static Event getEvent() {
        Event event = new Event();
        event.put("a", 1);
        event.put("b", 17);
        return event;
    }

    @Test
    public void testProcessMultiple() throws Exception {

        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(TransformerConfigurationTest.build("Task"));
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
                Assert.assertEquals(18, result.get(index).get("c"));
            }

            task.stop();
        }
        finally {
            streamManager.stop();
        }
    }
}