package stream.machine.core.worker;

/**
 * Created by Stephane on 14/02/2015.
 */
public interface WorkerFactory {
    Worker build(WorkerType workerType,String workerName);
}