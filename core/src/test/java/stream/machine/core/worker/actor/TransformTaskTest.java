package stream.machine.core.worker.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
import stream.machine.core.configuration.transform.EventTransformerConfigurationTest;
import stream.machine.core.model.Event;
import stream.machine.core.task.Task;
import stream.machine.core.task.actor.TransformTask;

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

        Task transformTask = new TransformTask(EventTransformerConfigurationTest.build("Simple"), system);

        transformTask.start();
        Event event = new Event();
        event.put("a", 1);
        event.put("b", 17);

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Future<Event> future = transformTask.process(event);
        Event result = Await.result(future, timeout.duration());

        Assert.assertEquals(18, result.get("c"));
        transformTask.stop();
    }
}