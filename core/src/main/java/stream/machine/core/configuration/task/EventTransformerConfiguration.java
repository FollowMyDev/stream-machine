package stream.machine.core.configuration.task;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConfigurationType;

/**
 * Created by Stephane on 04/01/2015.
 */
public class EventTransformerConfiguration extends Configuration {
    private String template;

    public EventTransformerConfiguration()
    {}

    public EventTransformerConfiguration(String name, String template) {
        super(name, ConfigurationType.EventTransformer);
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
