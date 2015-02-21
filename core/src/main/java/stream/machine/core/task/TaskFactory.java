package stream.machine.core.task;

import java.util.Map;

/**
 * Created by Stephane on 14/02/2015.
 */
public interface TaskFactory {
    Task build(TaskType taskType,String taskName);
    Map<String,Task> buildAll(TaskType taskType);
}
