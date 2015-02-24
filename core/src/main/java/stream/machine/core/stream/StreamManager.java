package stream.machine.core.stream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSingletonManager;
import akka.contrib.pattern.ClusterSingletonProxy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.StoreManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskType;
import stream.machine.core.task.actor.ActorTaskFactory;

import java.util.Map;

/**
 * Created by Stephane on 14/02/2015.
 */
public class StreamManager extends ManageableBase {
    private final int communicationPort;
    private final StoreManager storeManager;
    private ActorSystem system;
    private ActorRef watcher;
    private ActorRef localMaster;
    private ActorRef master;
    private ActorTaskFactory factory;

    public StreamManager(StoreManager storeManager, int communicationPort) {
        super("StreamManager");
        this.storeManager = storeManager;
        this.communicationPort = communicationPort;
    }

    @Override
    public void start() throws ApplicationException {
        // Override the configuration of the port
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + communicationPort)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = " + "[master,worker]"))
                .withFallback(ConfigFactory.load("master"));


        this.system = ActorSystem.create(getName(), config);

        factory = new ActorTaskFactory(storeManager, system);

        watcher = system.actorOf(Props.create(StreamWatcher.class), "watcher");

        localMaster = system.actorOf(ClusterSingletonManager.defaultProps(Props.create(StreamMaster.class), "master", PoisonPill.getInstance(), "master"), "streamService");

        master = system.actorOf(ClusterSingletonProxy.defaultProps("user/streamService/master", "master"), "masterProxy");


    }

    @Override
    public void stop() throws ApplicationException {
        system.shutdown();
    }


    public Task getTask(String workerName, TaskType taskType) throws ApplicationException {
        return factory.build(taskType, workerName);
    }

    public Map<String, Task> getTasks(TaskType taskType) throws ApplicationException {
        return factory.buildAll(taskType);
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }
}
