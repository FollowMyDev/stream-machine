package stream.machine.core.monitor.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import stream.machine.core.monitor.ConfigurationMessage;
import stream.machine.core.monitor.MonitorConsumer;
import stream.machine.core.monitor.RegisterMessage;

import java.lang.reflect.Constructor;

/**
 * Created by Stephane on 28/02/2015.
 */
public class MonitorSubscriber extends UntypedActor {
    public static Props props(final MonitorConsumer consumer, final String topic) {
        return Props.create(new Creator<MonitorSubscriber>() {
            @Override
            public MonitorSubscriber create() throws Exception {
                Constructor<MonitorSubscriber> ctor = MonitorSubscriber.class.getDeclaredConstructor(MonitorConsumer.class, String.class);
                MonitorSubscriber subscriber = ctor.newInstance(consumer, topic);
                return subscriber;
            }
        });
    }

    private final MonitorConsumer consumer;
    private final String topic;


    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public MonitorSubscriber(MonitorConsumer consumer, String topic) {
        this.consumer = consumer;
        this.topic = topic;


    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConfigurationMessage) {
            log.info("Receive message " + getSelf().path().toStringWithoutAddress());
            ConfigurationMessage configurationMessage = (ConfigurationMessage) message;
            if (consumer != null) {
                consumer.onMessage(configurationMessage);
            }
        } else if (message instanceof RegisterMessage) {
            log.info("Ask for subscription");

            getSender().tell("ok",getSelf());
        } else if (message instanceof DistributedPubSubMediator.SubscribeAck) {
            log.info("Subscription validated");
        } else
            unhandled(message);
    }
}
