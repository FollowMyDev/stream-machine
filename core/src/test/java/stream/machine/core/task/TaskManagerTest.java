package stream.machine.core.task;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.junit.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.task.TaskChainConfiguration;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.configuration.task.TaskManagerConfiguration;
import stream.machine.core.configuration.task.TransformerTaskConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.message.DataMessage;
import stream.machine.core.message.ErrorMessage;
import stream.machine.core.message.Message;
import stream.machine.core.model.Event;
import stream.machine.core.task.store.ConfigurationStore;
import stream.machine.core.task.store.configuration.MemoryConfigurationStore;
import stream.machine.core.task.transform.TransformerTask;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {

    private static ActorSystem system;
    private ActorRef taskManager;

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

    @Before
    public void setUp() {
        system = ActorSystem.create("TransformTasks");
    }

    @After
    public void tearDown() {
        system.stop(taskManager);
        system.shutdown();
    }

    @Test
    public void testFullLifecycle() throws Exception {
        ConfigurationStore configurationStore = new MemoryConfigurationStore();
        TaskConfiguration configurationTaskD = build("TaskD","#sum( \"b\" \"a\" \"d\")");
        configurationStore.saveTask(configurationTaskD);
        TaskConfiguration configurationTaskC = build("TaskC","#sum( \"c\" \"c\" \"c\")");
        configurationStore.saveTask(configurationTaskC);
        TaskConfiguration configurationTaskB = build("TaskB","#sum( \"b\" \"a\" \"c\")");
        configurationStore.saveTask(configurationTaskB);
        TaskConfiguration configurationTaskA = build("TaskA","#sum( \"a\" \"a\" \"b\")");
        configurationStore.saveTask(configurationTaskA);

        TaskChainConfiguration taskChainConfigurationD = new TaskChainConfiguration("TaskD", null);
        TaskChainConfiguration taskChainConfigurationC = new TaskChainConfiguration("TaskC", taskChainConfigurationD);
        TaskChainConfiguration taskChainConfigurationB = new TaskChainConfiguration("TaskB", taskChainConfigurationC);
        TaskChainConfiguration taskChainConfigurationA = new TaskChainConfiguration("TaskA", taskChainConfigurationB);

        TaskManagerConfiguration taskManagerConfiguration = new TaskManagerConfiguration("TaskManager","",taskChainConfigurationA,5);
        configurationStore.saveTaskManager(taskManagerConfiguration);

        taskManager = system.actorOf(TaskManager.props("TaskManager",configurationStore),"TaskManager");

        Event event = new Event();
        event.put("a", 1);

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Message message = new DataMessage<Event>("/deadLetters",event);
        Future<Object> futureA= Patterns.ask(taskManager, message, timeout);
        DataMessage<Event> result = (DataMessage<Event>) Await.result(futureA, timeout.duration());

        Assert.assertEquals(1, result.getData().get("a"));
        Assert.assertEquals(2, result.getData().get("b"));
        Assert.assertEquals(6, result.getData().get("c"));
        Assert.assertEquals(3, result.getData().get("d"));

    }

    @Test
    public void testTaskError() throws Exception {
        ConfigurationStore configurationStore = new MemoryConfigurationStore();
        TaskConfiguration configurationTaskB = buildException("TaskB","Oops, I Crashed");
        configurationStore.saveTask(configurationTaskB);
        TaskConfiguration configurationTaskA = build("TaskA","#sum( \"k\" \"k\" \"b\")");
        configurationStore.saveTask(configurationTaskA);

        TaskChainConfiguration taskChainConfigurationB = new TaskChainConfiguration("TaskB", null);
        TaskChainConfiguration taskChainConfigurationA = new TaskChainConfiguration("TaskA", taskChainConfigurationB);

        TaskManagerConfiguration taskManagerConfiguration = new TaskManagerConfiguration("TaskManager","",taskChainConfigurationA,5);
        configurationStore.saveTaskManager(taskManagerConfiguration);

        taskManager = system.actorOf(TaskManager.props("TaskManager",configurationStore),"TaskManager");

        Event event = new Event();
        event.put("a", 1);

        Timeout timeout = new Timeout(Duration.create(3000, "seconds"));
        Message message = new DataMessage<Event>("/deadLetters",event);
        Future<Object> futureA= Patterns.ask(taskManager, message, timeout);
        ErrorMessage result = (ErrorMessage) Await.result(futureA, timeout.duration());

        Assert.assertEquals("Oops, I Crashed", result.getErrorMessage());


    }

    private TaskConfiguration build(String taskName,String taskTemplate)
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

        return new TransformerTaskConfiguration(template.toString(),taskName,"stream.machine.core.task.transform.EventTransformerTask");

    }
    private TaskConfiguration buildException(String taskName,String errorMessage)
    {
        return new TransformerTaskConfiguration(errorMessage,taskName,"stream.machine.core.task.ExceptionTask");
    }
}