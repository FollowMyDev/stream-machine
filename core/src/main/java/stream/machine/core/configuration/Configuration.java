package stream.machine.core.configuration;

import scala.Serializable;
import stream.machine.core.task.TaskType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stephane on 18/01/2015.
 */
public class Configuration extends ConcurrentHashMap<String, Object> implements Serializable {

    public static String name = "name";
    public static String type = "type";
    public static String version = "version";


    public Configuration() {
    }

    public Configuration(Configuration other) {
        if (other != null) {
            this.putAll(other);
        }
    }

    public Configuration(String name, TaskType type) {
        put(Configuration.name, name);
        put(Configuration.type, type);
        put(Configuration.version, 0);
    }

    public String getName() {
        return (String) get(Configuration.name);
    }

    public TaskType getType() {
        return TaskType.valueOf(get(Configuration.type).toString());
    }

    public int getVersion() {
        return (Integer) get(Configuration.version);
    }
}
