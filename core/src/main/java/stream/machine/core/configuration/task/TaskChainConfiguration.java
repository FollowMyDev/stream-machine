package stream.machine.core.configuration.task;

/**
 * Created by Stephane on 13/01/2015.
 */
public class TaskChainConfiguration {

    private final String name;
    private final TaskChainConfiguration subTask;

    public TaskChainConfiguration(String name) {
        this.name = name;
        this.subTask = null;
    }

    public TaskChainConfiguration(String name,  TaskChainConfiguration subTask) {
        this.name = name;
        this.subTask = subTask;
    }

    public String getName() {
        return name;
    }

    public TaskChainConfiguration getSubTask() {
        return subTask;
    }
}
