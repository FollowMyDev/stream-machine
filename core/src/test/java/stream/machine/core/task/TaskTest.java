package stream.machine.core.task;

import org.junit.*;
import stream.machine.core.configuration.TransformerConfigurationTest;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.StoreManager;
import stream.machine.core.store.memory.MemoryStoreManager;
import stream.machine.core.stream.StreamManager;

import java.util.Map;

public class TaskTest extends ManageableBase{

    protected StreamManager streamManager;

    public TaskTest() {
        super("TaskTest");
    }

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

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
            Map<String,Task> tasks = streamManager.getTaskFactory().buildAll(TaskType.Transform);
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
        storeManager.getConfigurationStore().saveConfiguration(TransformerConfigurationTest.build("TaskA"));
        storeManager.getConfigurationStore().saveConfiguration(TransformerConfigurationTest.build("TaskB"));
        streamManager = new StreamManager(storeManager,null,"127.0.0.1",0, 2);
        streamManager.start();
    }

    @Override
    public void stop() throws ApplicationException {
        streamManager.stop();
        streamManager = null;
    }
}