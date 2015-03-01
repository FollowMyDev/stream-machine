package stream.machine.core.task;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;

import java.util.Map;

/**
 * Created by Stephane on 14/02/2015.
 */
public interface TaskFactory {
    Task build(String taskName) throws ApplicationException;
    Map<String,Task> buildAll(TaskType taskType) throws ApplicationException;
    Task build(Configuration configuration);
}
