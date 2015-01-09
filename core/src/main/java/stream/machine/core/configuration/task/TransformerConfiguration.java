package stream.machine.core.configuration.task;

import stream.machine.core.configuration.task.TaskConfiguration;

import java.util.List;

/**
 * Created by Stephane on 04/01/2015.
 */
public class TransformerConfiguration extends TaskConfiguration {
    private final String template;

    public TransformerConfiguration(String template,String name, String taskClass, List<TaskConfiguration> subTasks) {
        super(name, taskClass, subTasks);
        this.template = template;
    }


    public String getTemplate() {
        return template;
    }
}
