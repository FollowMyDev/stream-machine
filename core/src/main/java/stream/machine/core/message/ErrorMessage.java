package stream.machine.core.message;

import stream.machine.core.task.TaskStatus;

/**
 * Created by Stephane on 04/01/2015.
 */
public class ErrorMessage extends MessageBase {
    private final String errorMessage;

    public ErrorMessage(String task,String errorMessage) {
        super(task);
        this.errorMessage = errorMessage;
        setStatus(TaskStatus.ERROR);
    }

    public ErrorMessage(String task, Message message, String errorMessage) {
        super(task,message);
        this.errorMessage = errorMessage;
        setStatus(TaskStatus.ERROR);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
