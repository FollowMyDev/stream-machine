package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.sequence.SequenceConfigurationTest;
import stream.machine.core.configuration.store.EventStorageConfigurationTest;
import stream.machine.core.configuration.transform.EventTransformerConfigurationTest;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryConfigurationStore;
import stream.machine.core.store.memory.MemoryEventStore;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;

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
        storeManager.getConfigurationStore().saveConfiguration(EventTransformerConfigurationTest.build("TaskA"));
        storeManager.getConfigurationStore().saveConfiguration(EventStorageConfigurationTest.build("TaskB"));
        taskFactory = new ActorTaskFactory(storeManager,system);
        sequenceTask = new SequenceTask("SequenceTaskTest", SequenceConfigurationTest.build("Simple"),taskFactory, system);
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

        Event eventA = new Event("TestName", "TypeA");
        eventA.put("a", 12);
        eventA.put("b", 11);

        Future<Event> futureA =  sequenceTask.process(eventA);
        Timeout timeout = new Timeout(Duration.create(1000, "seconds"));
        Event resultA = Await.result(futureA, timeout.duration());
        Assert.assertTrue(!resultA.containsKey(sequenceTask.getErrorField()));
    }
}