package stream.machine.core.task.transform;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.task.TransformerTaskConfiguration;
import stream.machine.core.model.Event;
import stream.machine.core.message.DataMessage;
import stream.machine.core.message.Message;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskStatus;

public class EventTransformerTaskTest {

    private static ActorSystem system;
    private ActorRef transformerTask;

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
        system.stop(transformerTask);
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

        TransformerTaskConfiguration configuration = new TransformerTaskConfiguration(template.toString(),"TransformerTask","stream.machine.core.task.transform.EventTransformerTask");
        transformerTask = system.actorOf(Task.props(configuration,ActorRef.noSender(), null), configuration.getTaskClass());

        Event event = new Event();
        event.put("a", 1);
        event.put("b", 17);

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Message message = new DataMessage<Event>("TaskManager",event);
        Future<Object> future= Patterns.ask(transformerTask,message , timeout);
        DataMessage<Event> result = (DataMessage<Event>) Await.result(future, timeout.duration());

        Assert.assertEquals(18, result.getData().get("c"));
    }
}