package stream.machine.core.monitor.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import stream.machine.core.monitor.Message;

/**
 * Created by Stephane on 28/02/2015.
 */
public class MonitorPublisher extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    public void onReceive(Object message) {
        if (message instanceof Message) {
            Message monitorMessage = ((Message) message);
            log.info("Send broadcast message ...");
            mediator.tell(new DistributedPubSubMediator.Send("/user/Consumer1-Create", monitorMessage), getSelf());
            log.info("... broadcast message sent.");
        } else {
            unhandled(message);
        }
    }
}
