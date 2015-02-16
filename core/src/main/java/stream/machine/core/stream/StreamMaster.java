package stream.machine.core.stream;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

import java.lang.reflect.Constructor;

/**
 * Created by Stephane on 14/02/2015.
 */
/**
 *  This class is a singleton that implement services at cluster level
 */
public class StreamMaster extends UntypedActor {
    private final LoggingAdapter log;
    private final Cluster cluster;

    public static Props props() {
        return Props.create(new Creator<StreamMaster>() {
            @Override
            public StreamMaster create() throws Exception {
                Constructor<StreamMaster> ctor = StreamMaster.class.getDeclaredConstructor();
                StreamMaster service = ctor.newInstance();
                return service;
            }
        });
    }

    public StreamMaster() {
        log = Logging.getLogger(getContext().system(), this);
        cluster = Cluster.get(getContext().system());
    }

    @Override
    public void preStart() {



    }

    @Override
    public void postStop() {

    }

    @Override
    public void onReceive(Object message) {

        //Do job
        log.debug("Message received");

    }

}