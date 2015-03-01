package stream.machine.core.stream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSingletonManager;
import akka.contrib.pattern.ClusterSingletonProxy;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.monitor.Message;
import stream.machine.core.monitor.MonitorConsumer;
import stream.machine.core.monitor.MonitorProducer;
import stream.machine.core.monitor.RegisterMessage;
import stream.machine.core.monitor.actor.MonitorPublisher;
import stream.machine.core.monitor.actor.MonitorSubscriber;
import stream.machine.core.store.StoreManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;
import stream.machine.core.task.TaskType;
import stream.machine.core.task.actor.ActorTaskFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Stephane on 14/02/2015.
 */
public class StreamManager extends ManageableBase implements MonitorProducer {
    private final int communicationPort;
    private final StoreManager storeManager;
    private final String seeds;
    private final String hostname;
    private ActorSystem system;
    private ActorRef publisher;
    private Queue<ActorRef> consumers;
    private ActorRef watcher;
    private ActorRef localMaster;
    private ActorRef master;
    private ActorTaskFactory factory;

    public StreamManager(StoreManager storeManager, String seeds, String hostname, int communicationPort) {
        super("StreamManager");
        this.storeManager = storeManager;
        this.communicationPort = communicationPort;
        this.consumers = new ConcurrentLinkedQueue<ActorRef>();
        this.seeds = seeds;
        this.hostname = hostname;
    }

    @Override
    public void start() throws ApplicationException {
        // Override the configuration of the port
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + communicationPort)
                .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + hostname))
                .withFallback(ConfigFactory.parseString("akka.cluster.seed-nodes = " + seeds))
                .withFallback(ConfigFactory.load("master"));


        this.system = ActorSystem.create(getName(), config);

        factory = new ActorTaskFactory(storeManager, system);

        watcher = system.actorOf(Props.create(StreamWatcher.class), "watcher");

        publisher = system.actorOf(Props.create(MonitorPublisher.class), "publisher");

        localMaster = system.actorOf(ClusterSingletonManager.defaultProps(Props.create(StreamMaster.class), "master", PoisonPill.getInstance(), "master"), "streamService");

        master = system.actorOf(ClusterSingletonProxy.defaultProps("user/streamService/master", "master"), "masterProxy");
    }

    @Override
    public void stop() throws ApplicationException {
        system.shutdown();
    }

    public TaskFactory getTaskFactory() {
        return factory;
    }

    public void send(Message query) throws ApplicationException {
        if (publisher != null) {
            try {
                publisher.tell(query, watcher);
                logger.info("Message sent");
            } catch (Exception error) {
                throw new ApplicationException("Sending message failed", error);
            }
        }
    }

    public void register(String topic, MonitorConsumer consumer) {
        if (topic != null && consumer != null) {
            ActorRef subscriber = system.actorOf(MonitorSubscriber.props(consumer, topic), String.format("%s-%s", consumer.getName(), topic));
            consumers.add(subscriber);
            ActorRef mediator = DistributedPubSubExtension.get(system).mediator();
            mediator.tell(new DistributedPubSubMediator.Put(subscriber), subscriber);
        }
    }

    public Task getTask(String taskName) throws ApplicationException {
        return factory.build(taskName);
    }

    public Map<String, Task> getTasks(TaskType taskType) throws ApplicationException {
        return factory.buildAll(taskType);
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }


}
