package stream.machine.core.message;

import stream.machine.core.task.TaskStatus;

/**
 * Created by Stephane on 06/01/2015.
 */
public abstract class MessageBase implements Message {
    private TaskStatus status;
    private String task;
    ;
    private final MessageType type;

    public MessageBase(String task) {
        this.status = TaskStatus.INITIAL;
        this.type = MessageType.QUERY;
    }

    public MessageBase(String task, Message message) {
        this.task = task;
        this.type = MessageType.REPLY;
        if (message != null) {
            this.status = message.getStatus();
        } else {
            this.status = TaskStatus.INITIAL;
        }
    }

    @Override
    public TaskStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }


    @Override
    synchronized public String getTask() {
        return task;
    }

    @Override
    synchronized public void setTask(String task) {
        this.task = task;
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
