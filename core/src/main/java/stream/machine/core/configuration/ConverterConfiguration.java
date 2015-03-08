package stream.machine.core.configuration;

import stream.machine.core.task.TaskType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stephane on 27/02/2015.
 */
public class ConverterConfiguration extends Configuration {
    public static String fieldsToConvert = "fieldsToConvert";

    public ConverterConfiguration() {
        super("", TaskType.Convert);
        put(ConverterConfiguration.fieldsToConvert,new ConcurrentHashMap<String, String>());
    }

    public ConverterConfiguration(String name, Map<String, String> fieldsToConvert) {
        super(name, TaskType.Convert);
        put(ConverterConfiguration.fieldsToConvert,fieldsToConvert);
    }

    public ConverterConfiguration(Configuration configuration) {
        super(configuration);
    }

    public Map<String, String> getFieldsToConvert() {
        return (Map<String, String>) get(ConverterConfiguration.fieldsToConvert);
    }
}