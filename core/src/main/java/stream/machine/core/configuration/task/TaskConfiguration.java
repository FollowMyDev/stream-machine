package stream.machine.core.configuration.task;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public class TaskConfiguration {
    private final String name;
    private final String taskClass;

    public TaskConfiguration(String name, String taskClass) {
        this.name = name;
        this.taskClass = taskClass;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public String getName() {
        return name;
    }
}
