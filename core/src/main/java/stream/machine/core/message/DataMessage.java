package stream.machine.core.message;

import stream.machine.core.task.TaskStatus;

/**
 * Created by Stephane on 03/01/2015.
 */
public class DataMessage<T> extends MessageBase {
    private final T data;

    public DataMessage(String task ,T data) {
        super(task);
        this.data = data;
    }

    public DataMessage(String task, Message message, T data) {
        super(task,message);
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

}
