package stream.machine.core.configuration.service;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConfigurationType;

/**
 * Created by Stephane on 18/01/2015.
 */
public class ServiceConfiguration extends Configuration {

    private int timeOutInMilliseconds;

    public ServiceConfiguration()
    {}

    public ServiceConfiguration(String name, int timeOutInMilliseconds) {
        super(name, ConfigurationType.Service);
        this.timeOutInMilliseconds = timeOutInMilliseconds;
    }

    public int getTimeOutInMilliseconds() {
        return timeOutInMilliseconds;
    }
}
