package stream.machine.core.stream;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import stream.machine.core.monitor.Message;

/**
 * Created by Stephane on 14/02/2015.
 */

/**
 * The class controls the consistency of the cluster. It centralizes all worker registrations to
 */
public class StreamWatcher extends UntypedActor {
    private final LoggingAdapter log;
    private final Cluster cluster;

    public StreamWatcher() {
        log = Logging.getLogger(getContext().system(), this);
        cluster = Cluster.get(getContext().system());
    }

    //subscribe to cluster changes
    @Override
    public void preStart() {
        //#subscribe
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
        //#subscribe

    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());

    }

    /**
     * @param message
     * @return
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp mUp = (ClusterEvent.MemberUp) message;
            log.info("Member is Up: {}", mUp.member());

        } else if (message instanceof ClusterEvent.UnreachableMember) {
            ClusterEvent.UnreachableMember mUnreachable = (ClusterEvent.UnreachableMember) message;
            log.info("Member detected as unreachable: {}", mUnreachable.member());

        } else if (message instanceof ClusterEvent.MemberRemoved) {
            ClusterEvent.MemberRemoved mRemoved = (ClusterEvent.MemberRemoved) message;
            log.info("Member is Removed: {}", mRemoved.member());

        } else if (message instanceof ClusterEvent.MemberEvent) {
            // ignore

        } else if (message instanceof Message) {

        } else {
            unhandled(message);
        }

    }
}