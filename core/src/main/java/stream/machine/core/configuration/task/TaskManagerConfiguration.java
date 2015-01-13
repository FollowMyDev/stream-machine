package stream.machine.core.configuration.task;

/**
 * Created by Stephane on 13/01/2015.
 */
public class TaskManagerConfiguration {
    private final String name;
    private final String taskManagerClass;
    private final TaskChainConfiguration subTasks;
    private final int timeoutInSeconds;

    public TaskManagerConfiguration(String name, String taskManagerClass, TaskChainConfiguration subTasks, int timeoutInSeconds) {
        this.name = name;
        this.taskManagerClass = taskManagerClass;
        this.subTasks = subTasks;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public String getName() {
        return name;
    }

    public String getTaskManagerClass() {
        return taskManagerClass;
    }

    public TaskChainConfiguration getSubTasks() {
        return subTasks;
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }
}
