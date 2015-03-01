package stream.machine.core.configuration;

import stream.machine.core.task.TaskType;

/**
 * Created by Stephane on 04/01/2015.
 */
public class TransformerConfiguration extends Configuration {
    public static String template = "template";

    public TransformerConfiguration() {
        super("", TaskType.Transform);
        put(TransformerConfiguration.template, null);
    }

    public TransformerConfiguration(Configuration configuration) {
        super(configuration);
    }

    public TransformerConfiguration(String name, String template) {
        super(name, TaskType.Transform);
        put(TransformerConfiguration.template, template);
    }

    public String getTemplate() {
        return (String) get(TransformerConfiguration.template);
    }
}
