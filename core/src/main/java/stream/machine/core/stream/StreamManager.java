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
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.worker.TransformMessage;
import stream.machine.core.worker.Worker;
import stream.machine.core.worker.WorkerType;
import stream.machine.core.worker.actor.ActorWorkerFactory;
import stream.machine.core.worker.actor.TransformWorker;

/**
 * Created by Stephane on 14/02/2015.
 */
public class StreamManager extends ManageableBase {
    private final int communicationPort;
    private final StoreManager storeManager;
    private ActorSystem system;
    private ActorRef worker;
    private ActorRef watcher;
    private ActorRef localMaster;
    private ActorRef master;
    private ActorWorkerFactory factory;

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

        factory = new ActorWorkerFactory(storeManager, system);

        watcher = system.actorOf(Props.create(StreamWatcher.class), "watcher");

        worker = system.actorOf(StreamWorker.props("worker", WorkerType.Transform, null), "worker");

        localMaster = system.actorOf(ClusterSingletonManager.defaultProps(Props.create(StreamMaster.class), "master", PoisonPill.getInstance(), "master"), "streamService");

        master = system.actorOf(ClusterSingletonProxy.defaultProps("user/streamService/master", "master"), "masterProxy");


    }

    @Override
    public void stop() throws ApplicationException {
        system.shutdown();
    }


    public Worker getWorker(String workerName, WorkerType workerType) {
        return factory.build(workerType, workerName);
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }
}
