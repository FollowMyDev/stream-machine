package stream.machine.core.configuration.task;

import stream.machine.core.configuration.task.TaskConfiguration;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public class TransformerTaskConfiguration extends TaskConfiguration {
    private final String template;

    public TransformerTaskConfiguration(String template, String name, String taskClass) {
        super(name, taskClass);
        this.template = template;
    }


    public String getTemplate() {
        return template;
    }
}
