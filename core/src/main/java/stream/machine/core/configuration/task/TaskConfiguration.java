package stream.machine.core.configuration.task;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public class TaskConfiguration {
    private final String name;
    private final String taskClass;
    private final List<TaskConfiguration> subTasks;

    public TaskConfiguration(String name, String taskClass, List<TaskConfiguration> subTasks) {
        this.name = name;
        this.taskClass = taskClass;
        this.subTasks = subTasks;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public List<TaskConfiguration> getSubTasks() {
        return subTasks;
    }

    public String getName() {
        return name;
    }
}
