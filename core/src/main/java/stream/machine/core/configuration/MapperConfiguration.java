package stream.machine.core.configuration;

import stream.machine.core.task.TaskType;

import java.util.Map;

/**
 * Created by Stephane on 27/02/2015.
 */
public class MapperConfiguration extends Configuration {
    public static String fieldsToMap = "fieldsToMap";

    public MapperConfiguration() {
        super("", TaskType.Map);
        put(MapperConfiguration.fieldsToMap,null);
    }

    public MapperConfiguration(Configuration configuration) {
        super(configuration);
    }

    public MapperConfiguration(String name, Map<String, String> fieldsToMap) {
        super(name, TaskType.Map);
        put(MapperConfiguration.fieldsToMap,fieldsToMap);
    }

    public Map<String, String> getFieldsToMap() {
        return (Map<String, String>) get(MapperConfiguration.fieldsToMap);
    }
}