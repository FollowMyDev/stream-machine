package stream.machine.core.message;

import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.TaskStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephane on 06/01/2015.
 */
public class StatusTable {
    private final Map<String, TaskStatus> status;

    public StatusTable() {
        this.status = new HashMap<String, TaskStatus>();
    }

    synchronized public TaskStatus getStatus() throws ApplicationException {
        int initial = 0;
        int processing = 0;
        int done = 0;
        int error = 0;
        for (TaskStatus status : this.status.values()) {
            switch (status) {
                case INITIAL:
                    initial++;
                    break;
                case PROCESSING:
                    processing++;
                    break;
                case DONE:
                    done++;
                    break;
                case ERROR:
                    error++;
                    break;
            }
        }
        if (error > 0) return TaskStatus.ERROR;
        if (processing > 0) return TaskStatus.PROCESSING;
        if (initial > 0 && done == 0) return TaskStatus.INITIAL;
        if (done > 0) return TaskStatus.DONE;
        throw new ApplicationException("This message has an invalid state");
    }

    synchronized public TaskStatus getStatus(String task) throws ApplicationException {
        if (this.status.containsKey(task)) return this.status.get(task);
        return TaskStatus.UNDEFINED;
    }

    synchronized public void setStatus(String task, TaskStatus status) {
        this.status.put(task, status);
    }


}
