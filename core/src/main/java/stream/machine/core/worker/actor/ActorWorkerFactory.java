package stream.machine.core.worker.actor;

import akka.actor.ActorSystem;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.configuration.task.EventTransformerConfiguration;
import stream.machine.core.store.StoreManager;
import stream.machine.core.worker.Worker;
import stream.machine.core.worker.WorkerFactory;
import stream.machine.core.worker.WorkerType;

/**
 * Created by Stephane on 14/02/2015.
 */
public class ActorWorkerFactory implements WorkerFactory {
    private final StoreManager storeManager;
    private final ActorSystem system;

    public ActorWorkerFactory(StoreManager storeManager, ActorSystem system) {
        this.storeManager = storeManager;
        this.system = system;
    }

    @Override
    public Worker build(WorkerType workerType, String workerName) {
        switch (workerType) {
            case Transform:
                EventTransformerConfiguration configuration = storeManager.getConfigurationStore().readConfiguration(workerName, ConfigurationType.EventTransformer, EventTransformerConfiguration.class);
                if ( configuration == null )
                {
                    configuration = new EventTransformerConfiguration(workerName,"");
                }
                return  new TransformWorker(configuration,this.system);
        }
        return null;
    }
}
