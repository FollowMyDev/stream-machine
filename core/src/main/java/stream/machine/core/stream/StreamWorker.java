package stream.machine.core.stream;


import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;
import stream.machine.core.task.TaskType;

import java.lang.reflect.Constructor;

/**
 * Created by Stephane on 14/02/2015.
 */

/**
 * This actor dispatches elementary job to a cluster of worker
 */
public class StreamWorker extends UntypedActor {

    private Task worker;
    private final String workerName;
    private final TaskType workerType;
    private final TaskFactory workerFactory;

    public static Props props(final String workerName, final TaskType workerType, final TaskFactory workerFactory) {
        return Props.create(new Creator<StreamWorker>() {
            @Override
            public StreamWorker create() throws Exception {
                Constructor<StreamWorker> ctor = StreamWorker.class.getDeclaredConstructor(String.class, TaskType.class, TaskFactory.class);
                StreamWorker worker = ctor.newInstance(workerName, workerType, workerFactory);
                return worker;
            }
        });
    }

    public StreamWorker(String workerName, TaskType workerType, TaskFactory workerFactory) {
        this.workerName = workerName;
        this.workerType = workerType;
        this.workerFactory = workerFactory;
    }

    @Override
    public void preStart() {
        if (workerFactory != null) {
            worker = null;
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