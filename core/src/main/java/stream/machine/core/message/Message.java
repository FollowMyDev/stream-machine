package stream.machine.core.message;

import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.TaskStatus;

/**
 * Created by Stephane on 04/01/2015.
 */
public interface Message extends Cloneable{
    StatusTable getStatusTable();
    ErrorTable getErrorTable();
    String getTask();
    void setTask(String task);
    MessageType getType();

}
