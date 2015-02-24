package stream.machine.core.configuration.store;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.task.TaskType;

/**
 * Created by Stephane on 18/01/2015.
 */
public class EventStorageConfiguration extends Configuration {

    private final int timeOutInMilliseconds;
    private final int bulkSize;
    private final int bulkPeriodInMilliseconds;

    public EventStorageConfiguration()
    {
        this.timeOutInMilliseconds = 0;
        this.bulkSize = 0;
        this.bulkPeriodInMilliseconds = 0;
    }

    public EventStorageConfiguration(String name, int timeOutInMilliseconds,int bulkSize, int bulkPeriodInMilliseconds) {
        super(name, TaskType.Store);
        this.timeOutInMilliseconds = timeOutInMilliseconds;
        this.bulkSize = bulkSize;
        this.bulkPeriodInMilliseconds = bulkPeriodInMilliseconds;
    }

    public int getTimeOutInMilliseconds() {
        return timeOutInMilliseconds;
    }

    public int getBulkSize() {
        return bulkSize;
    }

    public int getBulkPeriodInMilliseconds() {
        return bulkPeriodInMilliseconds;
    }
}
