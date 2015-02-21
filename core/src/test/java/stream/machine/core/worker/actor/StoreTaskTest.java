package stream.machine.core.worker.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.joda.time.DateTime;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.store.EventStorageConfigurationTest;
import stream.machine.core.configuration.transform.EventTransformerConfigurationTest;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;
import stream.machine.core.store.memory.MemoryEventStore;
import stream.machine.core.task.Task;
import stream.machine.core.task.actor.StoreTask;
import stream.machine.core.task.actor.TransformTask;

import java.util.List;

import static org.junit.Assert.*;

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
        Assert.assertTrue(!resultA.containsKey(Event.storeError));
        Future<Event> futureB =  storeTask.process(eventB);
        Event resultB = Await.result(futureB, timeout.duration());
        Assert.assertTrue(!resultB.containsKey(Event.storeError));

        List<Event> eventsOfTypeA = eventStore.fetch("TypeA",now.minusHours(1),now.plusHours(1));
        Assert.assertNotNull(eventsOfTypeA);
        Assert.assertEquals(1,eventsOfTypeA.size());
        Assert.assertEquals(12,eventsOfTypeA.get(0).get("a"));
        List<Event> eventsOfTypeB = eventStore.fetch("TypeB",now.minusHours(1),now.plusHours(1));
        Assert.assertNotNull(eventsOfTypeB);
        Assert.assertEquals(1,eventsOfTypeB.size());
        Assert.assertEquals(17,eventsOfTypeB.get(0).get("b"));

    }
}