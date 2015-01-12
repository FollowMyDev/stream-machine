package stream.machine.worker.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 * Created by Stephane on 07/01/2015.
 */
public class WorkerConfiguration extends Configuration {
    @JsonProperty(required = true)
    private String configurationStore;

    @JsonProperty
    private int timeoutInSeconds;

    public WorkerConfiguration(){
        // set default values for non required fields
        timeoutInSeconds = 5;
    }

    public String getConfigurationStore() {
        return configurationStore;
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }
}
