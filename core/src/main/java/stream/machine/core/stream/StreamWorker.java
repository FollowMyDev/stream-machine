package stream.machine.core.stream;


import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import stream.machine.core.worker.Worker;
import stream.machine.core.worker.WorkerFactory;
import stream.machine.core.worker.WorkerType;

import java.lang.reflect.Constructor;

/**
 * Created by Stephane on 14/02/2015.
 */

/**
 * This actor dispatches elementary job to a cluster of worker
 */
public class StreamWorker extends UntypedActor {

    private Worker worker;
    private final String workerName;
    private final WorkerType workerType;
    private final WorkerFactory workerFactory;

    public static Props props(final String workerName, final WorkerType workerType, final WorkerFactory workerFactory) {
        return Props.create(new Creator<StreamWorker>() {
            @Override
            public StreamWorker create() throws Exception {
                Constructor<StreamWorker> ctor = StreamWorker.class.getDeclaredConstructor(String.class, WorkerType.class, WorkerFactory.class);
                StreamWorker worker = ctor.newInstance(workerName, workerType, workerFactory);
                return worker;
            }
        });
    }

    public StreamWorker(String workerName, WorkerType workerType, WorkerFactory workerFactory) {
        this.workerName = workerName;
        this.workerType = workerType;
        this.workerFactory = workerFactory;
    }

    @Override
    public void preStart() {
        if (workerFactory != null) {
            worker = workerFactory.build(workerType, workerName);
        }

    }

    @Override
    public void postStop() {
        if (worker != null) {
            worker = null;
        }
    }

    @Override
    public void onReceive(Object message) {

        //Do job

    }

}