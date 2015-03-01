package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.EventStorageConfigurationTest;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryEventStore;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskType;

import java.util.List;

public class StoreTaskTest {
    private static ActorSystem system;
    private Task storeTask;
    private EventStore eventStore;

    @BeforeClass
    public static void oneTimeSetUp() {
        system = ActorSystem.create("StoreTaskTest");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        system.shutdown();
    }

    @Before
    public void setUp() throws ApplicationException {
        eventStore = new MemoryEventStore();
        storeTask = new StoreTask(eventStore,EventStorageConfigurationTest.build("Simple"), system);
        storeTask.start();
    }

    @After
    public void tearDown() throws ApplicationException {
        storeTask.stop();
        storeTask = null;
        eventStore = null;
    }
    @Test
    public void testProcess() throws Exception {
      Assert.assertNotNull(storeTask);
        Assert.assertNotNull(eventStore);
        DateTime now = new DateTime();

        Event eventA = new Event("TestName","TypeA");
        eventA.put("a", 12);

        Event eventB = new Event("TestName","TypeB");
        eventB.put("b", 17);

        Timeout timeout = new Timeout(Duration.create(1000, "seconds"));
        Future<Event> futureA =  storeTask.process(eventA);
        Event resultA = Await.result(futureA, timeout.duration());
        Assert.assertTrue(!resultA.containsKey(storeTask.getErrorField()));
        Future<Event> futureB =  storeTask.process(eventB);
        Event resultB = Await.result(futureB, timeout.duration());
        Assert.assertTrue(!resultB.containsKey(storeTask.getErrorField()));

        List<Event> eventsOfTypeA = eventStore.fetch("TypeA",now.minusHours(1),now.plusHours(1));
        Assert.assertNotNull(eventsOfTypeA);
        Assert.assertEquals(1,eventsOfTypeA.size());
        Assert.assertEquals(12,eventsOfTypeA.get(0).get("a"));
        List<Event> eventsOfTypeB = eventStore.fetch("TypeB",now.minusHours(1),now.plusHours(1));
        Assert.assertNotNull(eventsOfTypeB);
        Assert.assertEquals(1,eventsOfTypeB.size());
        Assert.assertEquals(17,eventsOfTypeB.get(0).get("b"));

    }

    @Test
    public void testProcessMultiple() throws Exception {

        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("Task"));
        StreamManager streamManager = new StreamManager(storeManager,"[\"akka.tcp://StreamManager@localhost:2550\",\"akka.tcp://StreamManager@localhost:2552\"]","localhost",2550);
        try {
            streamManager.start();
            Task task= streamManager.getTask("Task");
            task.start();
            ImmutableList.Builder<Event> builder  = new ImmutableList.Builder<Event>();
            for (int index=0; index < 1000; index++)
            {
                Event eventA = new Event("TestName","TypeA");
                eventA.put("a", 12);
                builder.add(eventA);
                Event eventB = new Event("TestName","TypeB");
                eventB.put("b", 17);
                builder.add(eventB);
            }

            Timeout timeout = new Timeout(Duration.create(1000, "seconds"));
            Future<List<Event>> future = task.processMultiple(builder.build());
            List<Event> result = Await.result(future, timeout.duration());

            Assert.assertEquals(2000, result.size());
            task.stop();
        }
        catch (Exception error)
        {
            Assert.fail(error.getMessage());
        }
        finally {
            streamManager.stop();
        }
    }
}