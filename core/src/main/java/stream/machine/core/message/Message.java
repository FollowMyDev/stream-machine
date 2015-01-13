package stream.machine.core.message;

import stream.machine.core.task.TaskStatus;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface Message extends Cloneable{
    TaskStatus getStatus();
    void setStatus(TaskStatus status);
    String getTask();
    void setTask(String task);
    MessageType getType();
}
