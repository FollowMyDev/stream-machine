package stream.machine.core.worker;

/**
 * Created by Stephane on 15/02/2015.
 */
public class WorkerMessage {
    private final WorkerType workerType;

    public WorkerMessage(WorkerType workerType) {
        this.workerType = workerType;
    }


    public WorkerType getWorkerType() {
        return workerType;
    }
}
