package stream.machine.core.configuration.sequence;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.task.TaskType;

import java.util.Map;

/**
 * Created by Stephane on 24/02/2015.
 */
public class SequenceConfiguration extends Configuration {
    private final Map<Integer, Configuration> taskConfigurations;


    public SequenceConfiguration(String name, Map<Integer, Configuration> taskConfigurations) {
        super(name, TaskType.Sequence);
        this.taskConfigurations = taskConfigurations;
    }

    public Map<Integer, Configuration> getTaskConfigurations() {
        return taskConfigurations;
    }
}
