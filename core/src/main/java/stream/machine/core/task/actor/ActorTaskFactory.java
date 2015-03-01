package stream.machine.core.task.actor;

import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableMap;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
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
    public Task build(String taskName) throws ApplicationException {
        Configuration configuration = storeManager.getConfigurationStore().readConfiguration(taskName);
        return build(configuration);
    }

    @Override
    public Map<String, Task> buildAll(TaskType taskType) throws ApplicationException {
        List<Configuration> configurations = storeManager.getConfigurationStore().readAll(taskType);
        if (configurations != null && configurations.size() > 0) {
            ImmutableMap.Builder<String, Task> builder = ImmutableMap.builder();
            for (Configuration configuration : configurations) {
                if (configuration != null) {
                    builder.put(configuration.getName(), build(configuration));
                }
            }
            return builder.build();
        }
        return null;
    }
    @Override
    public Task build(Configuration configuration) {
        if (configuration != null) {
            switch (configuration.getType()) {
                case Transform:
                    return new TransformTask(configuration, system);
                case Store:
                    return new StoreTask(storeManager.getEventStore(), configuration, system);
                case Convert:
                    return new ConverterTask(configuration, system);
                case Filter:
                    return new FilterTask(configuration, system);
                case Map:
                    return new MapperTask(configuration, system);
                case Sequence:
                    return new SequenceTask(configuration, this, system);
                case UserAgent:
                    return new UserAgentParserTask(configuration, system);
            }
        }
        return null;
    }
}
