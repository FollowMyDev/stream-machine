package stream.machine.core.configuration;

import stream.machine.core.task.TaskType;

import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 27/02/2015.
 */
public class FilterConfiguration extends Configuration {
    public static String fieldsToFilter = "fieldsToFilter";

    public FilterConfiguration() {
        super("", TaskType.Filter);
        put(FilterConfiguration.fieldsToFilter,null);
    }

    public FilterConfiguration(Configuration configuration) {
        super(configuration);
    }

    public FilterConfiguration(String name,  List<String> fieldsToFilter) {
        super(name, TaskType.Filter);
        put(FilterConfiguration.fieldsToFilter,fieldsToFilter);
    }

    public List<String> getFieldsToFilter()  {
        return (List<String>) get(FilterConfiguration.fieldsToFilter);
    }
}