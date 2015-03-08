package stream.machine.core.stream;


import com.google.common.util.concurrent.MoreExecutors;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.*;
import stream.machine.core.communication.MessageConsumer;
import stream.machine.core.communication.MessageProducer;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.store.StoreManager;
import stream.machine.core.task.StoredTaskFactory;
import stream.machine.core.task.TaskFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * Created by Stephane on 14/02/2015.
 */
public class StreamManager extends ManageableBase implements MessageProducer {
    private final int communicationPort;
    private final StoreManager storeManager;
    private final List<String> members;
    private final int concurrency;
    private final String hostname;
    private StoredTaskFactory factory;
    private HazelcastInstance instance;
    private Map<UUID, Event> events;

    private Thread masterThread;
    private AtomicBoolean isMaster;
    private CountDownLatch isRunning;
    private ExecutorService executor;

    public StreamManager(StoreManager storeManager,
                         List<String> members,
                         String hostname,
                         int communicationPort,
                         int concurrency) {
        super("StreamManager");
        this.storeManager = storeManager;
        this.communicationPort = communicationPort;
        this.members = members;
        this.hostname = hostname;
        this.concurrency = concurrency;
    }

    @Override
    public void start() throws ApplicationException {
        logger.info("Starting StreamManager ...");
        if (this.instance != null) {
            stop();
        }
        try {
            logger.info(String.format("Creating thread pool with %d threads", this.concurrency));
            if (this.concurrency <= 0) {
                throw new ApplicationException("Concurrency  must be greater than zero");
            }
            this.executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(this.concurrency));

            logger.info("Creating task factory");
            if (this.storeManager == null) {
                throw new ApplicationException("Store manager cannot be null");
            }
            this.factory = new StoredTaskFactory(this.storeManager, this.executor);

            logger.info("Creating distributed cache");
            Config configuration = buildConfig();
            this.instance = Hazelcast.newHazelcastInstance(configuration);
            this.isMaster = new AtomicBoolean(false);
            this.isRunning = new CountDownLatch(1);
            this.masterThread = new Thread() {
                @Override
                public void run() {
                    try {
                        if (instance == null || isRunning.getCount()==0) return;
                        do {
                            logger.debug("Trying to acquire master token ...");
                            Lock masterLock = instance.getLock("masterLock");

                            if (masterLock.tryLock(100, TimeUnit.MILLISECONDS)) {
                                try {
                                    isMaster.set(true);
                                    logger.info("Master token acquired ...");
                                    isRunning.await();
                                } finally {
                                    masterLock.unlock();
                                    isMaster.set(false);
                                }
                            } else {
                                isMaster.set(false);
                            }
                        }
                        while (!isRunning.await(100, TimeUnit.MILLISECONDS));
                    } catch (InterruptedException error) {
                        isMaster.set(false);
                    }

                }
            };
            this.masterThread.start();
        } finally {
            logger.info("... StreamManager started.");
        }


    }

    private Config buildConfig() {
        Config configuration = new Config();

        if (this.communicationPort != 0) {
            configuration.getNetworkConfig().setPort(this.communicationPort);
            configuration.getNetworkConfig().setPortAutoIncrement(false);
        } else {
            configuration.getNetworkConfig().setPortAutoIncrement(true);
        }

        if (this.members != null) {
            NetworkConfig network = configuration.getNetworkConfig();
            JoinConfig join = network.getJoin();
            join.getMulticastConfig().setEnabled(false);
            for (String member : this.members) {
                join.getTcpIpConfig().addMember(member);
            }
            join.getTcpIpConfig().setEnabled(true);
        }

//        MapConfig mapCfg = new MapConfig();
//        mapCfg.setName("events");
//        mapCfg.setBackupCount(2);
//        mapCfg.getMaxSizeConfig().setSize(1000000);
//        mapCfg.setTimeToLiveSeconds(300);

//        MapStoreConfig mapStoreCfg = new MapStoreConfig();
//        mapStoreCfg.setClassName("com.hazelcast.examples.DummyStore").setEnabled(true);
//        mapCfg.setMapStoreConfig(mapStoreCfg);

//        NearCacheConfig nearCacheConfig = new NearCacheConfig();
//        nearCacheConfig.setMaxSize(1000).setMaxIdleSeconds(120).setTimeToLiveSeconds(300);
//        mapCfg.setNearCacheConfig(nearCacheConfig);

//        configuration.addMapConfig(mapCfg);
        return configuration;
    }

    @Override
    public void stop() throws ApplicationException {
        if (this.instance != null) {
            this.isRunning.countDown();
            try {
                this.masterThread.join(10);
            } catch (InterruptedException error) {

            }
            this.instance.shutdown();
            this.instance = null;
        }
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }

    public TaskFactory getTaskFactory() {
        return factory;
    }

    public boolean isMaster() {
        return isMaster.get();
    }

    public boolean isRunning() {
        return (isRunning.getCount() > 0);
    }

    @Override
    public <T> void send(String topicName, T data) throws ApplicationException {
        logger.info(String.format("Sending data to topic %s ...", topicName));
        if (this.instance != null) {
            try {
                ITopic topic = instance.getTopic(topicName);
                if ( topic != null) {
                    topic.publish(data);
                    logger.info(String.format("... data sent to topic %s", topicName));
                }
            } catch (Exception error) {
                throw new ApplicationException("Sending message failed", error);
            } finally {
                logger.debug(String.format("Message sent on topic %s", topicName));
            }
        }
    }

    private class Listener<T> implements MessageListener<T> {
        private final MessageConsumer consumer;

        public Listener(MessageConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onMessage(Message<T> message) {
            if (this.consumer != null) {
                if ( message != null && message.getSource() != null) {
                    this.consumer.onMessage(message.getSource().toString(), message.getMessageObject());
                }
            }
        }
    }

    public void register(String topicName, MessageConsumer consumer) {
        if (topicName != null && consumer != null) {
            Listener listener = new Listener(consumer);
            if ( instance != null) {
                ITopic topic = instance.getTopic(topicName);
                if ( topic != null) {
                    topic.addMessageListener(listener);
                }
            }
        }
    }


}
