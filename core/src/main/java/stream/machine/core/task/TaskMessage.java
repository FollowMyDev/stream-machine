package stream.machine.core.task;

/**
 * Created by Stephane on 15/02/2015.
 */
public class TaskMessage {
    private final TaskType taskType;

    public TaskMessage(TaskType taskType) {
        this.taskType = taskType;
    }


    public TaskType getTaskType() {
        return taskType;
    }
}
