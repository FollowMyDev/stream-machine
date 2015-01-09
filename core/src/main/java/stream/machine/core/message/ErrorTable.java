package stream.machine.core.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephane on 06/01/2015.
 */
public class ErrorTable {
    private final Map<String, String> errorMessages;

    public ErrorTable() {
        this.errorMessages = new HashMap<String, String>();
    }

    synchronized public String getError(String task) {
        if (this.errorMessages.containsKey(task)) return this.errorMessages.get(task);
        return null;
    }

    synchronized public void setError(String task, String errorMessage) {
        this.errorMessages.put(task, errorMessage);
    }
}
