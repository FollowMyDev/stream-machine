package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.joda.time.DateTime;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.SequenceConfigurationTest;
import stream.machine.core.configuration.EventStorageConfigurationTest;
import stream.machine.core.configuration.TransformerConfigurationTest;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;

import java.util.List;

public class SequenceTaskTest {
    private static ActorSystem system;
    private Task sequenceTask;
    private StoreManager storeManager;
    private TaskFactory taskFactory;

    public SequenceTaskTest() {
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        system = ActorSystem.create("SequenceTaskTest");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        system.shutdown();
    }

    @Before
    public void setUp() throws ApplicationException {
        storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(TransformerConfigurationTest.build("TaskA"));
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("TaskB"));
        taskFactory = new ActorTaskFactory(storeManager,system);
        sequenceTask = new SequenceTask(SequenceConfigurationTest.build("SequenceTaskTest"),taskFactory, system);
        sequenceTask.start();
    }

    @After
    public void tearDown() throws ApplicationException {
        sequenceTask.stop();
        sequenceTask = null;
        storeManager = null;
    }

    @Test
    public void testProcess() throws Exception {
        Assert.assertNotNull(sequenceTask);

        DateTime now = new DateTime();
        Event eventA = new Event("TestName", "TypeA");
        eventA.put("a", 12);
        eventA.put("b", 11);

        Future<Event> futureA =  sequenceTask.process(eventA);
        Timeout timeout = new Timeout(Duration.create(1000, "seconds"));
        Event resultA = Await.result(futureA, timeout.duration());
        Assert.assertTrue(!resultA.containsKey(sequenceTask.getErrorField()));
        List<Event> eventsOfTypeA = storeManager.getEventStore().fetch("TypeA", now.minusHours(1), now.plusHours(1));
        Assert.assertNotNull(eventsOfTypeA);
        Assert.assertEquals(1,eventsOfTypeA.size());
        Assert.assertEquals(23,eventsOfTypeA.get(0).get("c"));
    }
}