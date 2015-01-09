package stream.machine.core.task;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TransformerConfiguration;
import stream.machine.core.message.DataMessage;
import stream.machine.core.message.Message;
import stream.machine.core.model.Event;
import stream.machine.core.task.store.ConfigurationStore;
import stream.machine.core.task.store.configuration.MemoryStore;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {
    private static ActorSystem system;
    private ActorRef taskManager;

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
        system.stop(taskManager);
    }

    @Test
    public void testFullLifecycle() throws Exception {
        ConfigurationStore configurationStore = new MemoryStore();
        TaskConfiguration configurationTaskD = build("TaskD","#sum( \"b\" \"a\" \"d\")",null);
        TaskConfiguration configurationTaskC = build("TaskC","#sum( \"c\" \"c\" \"c\")",null);
        List<TaskConfiguration> subTaskB = new ArrayList<TaskConfiguration>();
        subTaskB.add(configurationTaskD);
        subTaskB.add(configurationTaskC);
        TaskConfiguration configurationTaskB = build("TaskB","#sum( \"b\" \"a\" \"c\")",subTaskB);
        List<TaskConfiguration> subTaskA = new ArrayList<TaskConfiguration>();
        subTaskA.add(configurationTaskB);
        TaskConfiguration configurationTaskA = build("TaskA","#sum( \"a\" \"a\" \"b\")",subTaskA);
        configurationStore.save(configurationTaskA);
        taskManager = system.actorOf(TaskManager.props("TaskA",configurationStore),"TaskManager");

        Event event = new Event();
        event.put("a", 1);

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Message message = new DataMessage<Event>("/deadLetters",event);
        message.getStatusTable().setStatus("TaskA", TaskStatus.INITIAL);
        Future<Object> futureA= Patterns.ask(taskManager, message, timeout);
        DataMessage<Event> result = (DataMessage<Event>) Await.result(futureA, timeout.duration());

        Assert.assertEquals(1, result.getData().get("a"));
        Assert.assertEquals(2, result.getData().get("b"));
        Assert.assertEquals(6, result.getData().get("c"));
        Assert.assertEquals(3, result.getData().get("d"));

    }

    private TaskConfiguration build(String taskName,String taskTemplate, List<TaskConfiguration> subTasks)
    {
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
        template.append(taskTemplate);

       return new TransformerConfiguration(template.toString(),taskName,"stream.machine.core.task.transform.EventTransformerTask",subTasks);

    }
}