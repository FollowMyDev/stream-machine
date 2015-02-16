package stream.machine.core.configuration;

/**
 * Created by Stephane on 18/01/2015.
 */
public abstract class Configuration {
    private String name;
    private ConfigurationType type;
    private int version;

    protected Configuration(){
    }

    protected Configuration(String name, ConfigurationType type) {
        this.name = name;
        this.type = type;
        this.version = 0;
    }

    public String getName() {
        return name;
    }

    public ConfigurationType getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }
}
