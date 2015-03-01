package stream.machine.core.configuration;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.task.TaskType;

import java.util.Map;

/**
 * Created by Stephane on 24/02/2015.
 */
public class SequenceConfiguration extends Configuration {
    public static String taskConfigurations = "taskConfigurations";

    public SequenceConfiguration() {
        super("", TaskType.Sequence);
        put(SequenceConfiguration.taskConfigurations,null);
    }

    public SequenceConfiguration(Configuration configuration) {
        super(configuration);
    }

    public SequenceConfiguration(String name, Map<Integer, String> taskConfigurations) {
        super(name, TaskType.Sequence);
        put(SequenceConfiguration.taskConfigurations,taskConfigurations);
    }

    public Map<Integer, String> getTaskConfigurations() {
        return (Map<Integer, String>) get(SequenceConfiguration.taskConfigurations);
    }
}
