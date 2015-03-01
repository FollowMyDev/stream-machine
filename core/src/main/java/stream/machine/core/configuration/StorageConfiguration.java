package stream.machine.core.configuration;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.task.TaskType;

import java.util.Map;

/**
 * Created by Stephane on 18/01/2015.
 */
public class StorageConfiguration extends Configuration {

    public static String timeOutInMilliseconds = "timeOutInMilliseconds";
    public static String bulkSize = "bulkSize";
    public static String bulkPeriodInMilliseconds = "bulkPeriodInMilliseconds";

    public StorageConfiguration()
    {
        super("", TaskType.Store);
        put(StorageConfiguration.timeOutInMilliseconds,0);
        put(StorageConfiguration.bulkSize,0);
        put(StorageConfiguration.bulkPeriodInMilliseconds,0);
    }

    public StorageConfiguration(Configuration configuration) {
        super(configuration);
    }

    public StorageConfiguration(String name, int timeOutInMilliseconds, int bulkSize, int bulkPeriodInMilliseconds) {
        super(name, TaskType.Store);
        put(StorageConfiguration.timeOutInMilliseconds,timeOutInMilliseconds);
        put(StorageConfiguration.bulkSize,bulkSize);
        put(StorageConfiguration.bulkPeriodInMilliseconds,bulkPeriodInMilliseconds);
    }

    public int getTimeOutInMilliseconds() {
        return (Integer) get(StorageConfiguration.timeOutInMilliseconds);
    }

    public int getBulkSize() {
        return (Integer) get(StorageConfiguration.bulkSize);
    }

    public int getBulkPeriodInMilliseconds() {
        return (Integer) get(StorageConfiguration.bulkPeriodInMilliseconds);
    }
}
