package stream.machine.core.task;

import com.google.common.collect.ImmutableMap;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.store.StoreManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by Stephane on 14/02/2015.
 */
public class StoredTaskFactory implements TaskFactory {
    private final StoreManager storeManager;
    private final ExecutorService executor;

    public StoredTaskFactory(StoreManager storeManager, ExecutorService executor) {
        this.storeManager = storeManager;
        this.executor = executor;
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
                    return new TransformTask(configuration, executor);
                case Store:
                    return new StoreTask(storeManager.getEventStore(), configuration, executor);
                case Convert:
                    return new ConverterTask(configuration, executor);
                case Filter:
                    return new FilterTask(configuration, executor);
                case Map:
                    return new MapperTask(configuration, executor);
                case Sequence:
                    return new SequenceTask(configuration, this, executor);
                case UserAgent:
                    return new UserAgentParserTask(configuration, executor);
            }
        }
        return null;
    }
}
