package stream.machine.core.message;

/**
 * Created by Stephane on 06/01/2015.
 */
public abstract class MessageBase implements Message {
    private final StatusTable statusTable;
    private final ErrorTable errorTable;
    private String task;;
    private final MessageType type;

    public MessageBase(String task) {
        this.statusTable = new StatusTable();
        this.errorTable = new ErrorTable();
        this.task = task;
        this.type = MessageType.QUERY;
    }

    public MessageBase(String task, Message message) {
        this.task = task;
        this.type = MessageType.REPLY;
        if (message != null) {
            this.statusTable = message.getStatusTable();
            this.errorTable = message.getErrorTable();
        } else {
            this.statusTable = new StatusTable();
            this.errorTable = new ErrorTable();
        }
    }

    @Override
    public StatusTable getStatusTable() {
        return statusTable;
    }

    @Override
    public ErrorTable getErrorTable() {
        return errorTable;
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
