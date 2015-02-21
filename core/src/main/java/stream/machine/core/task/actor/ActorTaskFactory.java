package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableMap;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.configuration.store.EventStorageConfiguration;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
import stream.machine.core.store.StoreManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;
import stream.machine.core.task.TaskType;

import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 14/02/2015.
 */
public class ActorTaskFactory implements TaskFactory {
    private final StoreManager storeManager;
    private final ActorSystem system;

    public ActorTaskFactory(StoreManager storeManager, ActorSystem system) {
        this.storeManager = storeManager;
        this.system = system;
    }

    @Override
    public Task build(TaskType taskType, String workerName) {
        switch (taskType) {
            case Transform: {
                if (storeManager != null && storeManager.getConfigurationStore() != null) {
                    EventTransformerConfiguration configuration = storeManager.getConfigurationStore().readConfiguration(workerName, ConfigurationType.Transform, EventTransformerConfiguration.class);
                    if (configuration != null) {
                        return new TransformTask(configuration, this.system);
                    }
                }
            };
            break;
            case Store: {
                if (storeManager != null && storeManager.getConfigurationStore() != null) {
                    EventStorageConfiguration configuration = storeManager.getConfigurationStore().readConfiguration(workerName, ConfigurationType.Store, EventStorageConfiguration.class);
                    if (configuration != null) {
                        return new StoreTask(storeManager.getEventStore(),configuration, this.system);
                    }
                }
            };
            break;
        }
        return null;
    }

    @Override
    public Map<String, Task> buildAll(TaskType workerType) {
        ImmutableMap.Builder<String, Task> builder = ImmutableMap.builder();
        if (storeManager != null && storeManager.getConfigurationStore() != null) {
            switch (workerType) {
                case Transform: {
                    List<EventTransformerConfiguration> workerConfigurations = storeManager.getConfigurationStore().readAll(ConfigurationType.Transform, EventTransformerConfiguration.class);
                    if (workerConfigurations != null && workerConfigurations.size() > 0) {
                        for (EventTransformerConfiguration workerConfiguration : workerConfigurations) {
                            builder.put(workerConfiguration.getName(), new TransformTask(workerConfiguration, this.system));
                        }
                    }
                };
                break;
                case Store: {
                    List<EventStorageConfiguration> workerConfigurations = storeManager.getConfigurationStore().readAll(ConfigurationType.Store, EventStorageConfiguration.class);
                    if (workerConfigurations != null && workerConfigurations.size() > 0) {
                        for (EventStorageConfiguration workerConfiguration : workerConfigurations) {
                            builder.put(workerConfiguration.getName(), new StoreTask(storeManager.getEventStore(),workerConfiguration, this.system));
                        }
                    }
                };
                break;
            }
        }
        return builder.build();
    }
}
