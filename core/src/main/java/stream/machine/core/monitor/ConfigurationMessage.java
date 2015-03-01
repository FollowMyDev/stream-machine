package stream.machine.core.monitor;

import stream.machine.core.configuration.Configuration;

/**
 * Created by Stephane on 28/02/2015.
 */
public class ConfigurationMessage extends Message {
    private final Configuration configuration;

    public ConfigurationMessage(String subject, Configuration configuration) {
        super(subject);
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
