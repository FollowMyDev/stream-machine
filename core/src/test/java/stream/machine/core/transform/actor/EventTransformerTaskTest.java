package stream.machine.core.transform.actor;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.task.EventTransformerConfiguration;
import stream.machine.core.model.Event;
import stream.machine.core.worker.actor.TransformWorker;

public class EventTransformerTaskTest {

    private static ActorSystem system;

    @BeforeClass
    public static void oneTimeSetUp() {
        system = ActorSystem.create("TransformTasks");
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

    @org.junit.Test
    public void testTransform() throws Exception {
        StringBuilder template = new StringBuilder("");
        template.append("#macro( put $key $value )");
        template.append("#${event.put($key,$value)}");
        template.append("#end");
        template.append(" ");
        template.append("#macro( sum $keyA $keyB $keyC )");
        template.append("#set( $valueA = $event.get($keyA) )");
        template.append("#set( $valueB = $event.get($keyB) )");
        template.append("#set( $valueC = $valueA+$valueB )");
        template.append("#put( $keyC $valueC )");
        template.append("#end");
        template.append(" ");
        template.append("#sum( \"a\" \"b\" \"c\")");

        EventTransformerConfiguration configuration = new EventTransformerConfiguration("Simple",template.toString());
        TransformWorker transformerTask = new TransformWorker(configuration,system);
        transformerTask.start();
        Event event = new Event();
        event.put("a", 1);
        event.put("b", 17);

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Future<Event> future=  transformerTask.transform(event);
        Event result = Await.result(future, timeout.duration());

        Assert.assertEquals(18, result.get("c"));
        transformerTask.stop();
    }
}