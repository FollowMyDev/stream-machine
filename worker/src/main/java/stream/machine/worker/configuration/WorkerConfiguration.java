package stream.machine.worker.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import java.util.List;

/**
 * Created by Stephane on 07/01/2015.
 */
public class WorkerConfiguration extends Configuration {
    @JsonProperty(required = true)
    private String configurationStore;

    @JsonProperty(required = true)
    private String eventStore;

    @JsonProperty
    private int timeoutInSeconds;

    @JsonProperty
    private List<String> members;

    @JsonProperty
    private String hostname;

    @JsonProperty
    private int streamPort;

    @JsonProperty
    private int concurrency;

    public WorkerConfiguration(){
        // set default values for non required fields
        timeoutInSeconds = 5;
        concurrency = 20;
    }

    public String getConfigurationStore() {
        return configurationStore;
    }

    public String getEventStore() {
        return eventStore;
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public List<String> getMembers() {
        return members;
    }

    public int getStreamPort() {
        return streamPort;
    }

    public String getHostname() {
        return hostname;
    }

    public int getConcurrency() {
        return concurrency;
    }
}
