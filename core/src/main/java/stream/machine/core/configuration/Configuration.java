package stream.machine.core.configuration;

import stream.machine.core.task.TaskType;

/**
 * Created by Stephane on 18/01/2015.
 */
public class Configuration {
    private String name;
    private TaskType type;
    private int version;

    public Configuration(){
    }

    public Configuration(String name, TaskType type) {
        this.name = name;
        this.type = type;
        this.version = 0;
    }

    public String getName() {
        return name;
    }

    public TaskType getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }
}
