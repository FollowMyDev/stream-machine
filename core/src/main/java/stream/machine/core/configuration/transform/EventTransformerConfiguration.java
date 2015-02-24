package stream.machine.core.configuration.transform;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.task.TaskType;

/**
 * Created by Stephane on 04/01/2015.
 */
public class EventTransformerConfiguration extends Configuration {
    private String template;

    public EventTransformerConfiguration()
    {}

    public EventTransformerConfiguration(String name, String template) {
        super(name, TaskType.Transform);
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
