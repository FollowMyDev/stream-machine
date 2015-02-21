package stream.machine.core.worker;

import akka.actor.ActorSystem;
import org.junit.*;
import stream.machine.core.configuration.transform.EventTransformerConfigurationTest;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskType;

import java.util.Map;

public class TaskTest extends ManageableBase{

    private static ActorSystem system;
    StreamManager streamManager;

    public TaskTest() {
        super("TaskTest");
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        system = ActorSystem.create("TaskTest");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        system.shutdown();
    }

    @Before
    public void setUp() throws ApplicationException {
        start();
    }

    @After
    public void tearDown() throws ApplicationException {
        stop();
    }

    @Test
    public void testFullLifecycle() throws Exception {
        Assert.assertNotNull(streamManager);
        try {
            Map<String,Task> tasks = streamManager.getTasks(TaskType.Transform);
            Assert.assertNotNull(tasks);
            Assert.assertEquals(2,tasks.size());
            Assert.assertTrue(tasks.containsKey("TaskA"));
            Assert.assertTrue(tasks.containsKey("TaskB"));
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        }
    }

    @Override
    public void start() throws ApplicationException {
        StoreManager storeManager = new MemoryStoreManager();
        storeManager.getConfigurationStore().saveConfiguration(EventTransformerConfigurationTest.build("TaskA"));
        storeManager.getConfigurationStore().saveConfiguration(EventTransformerConfigurationTest.build("TaskB"));
        streamManager = new StreamManager(storeManager, 2550);
        streamManager.start();
    }

    @Override
    public void stop() throws ApplicationException {
        streamManager.stop();
        streamManager = null;
    }
}