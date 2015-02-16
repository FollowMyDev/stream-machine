package stream.machine.core.extension;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;

import java.io.File;

/**
 * Created by Stephane on 11/01/2015.
 */
public abstract class PluginBase extends Plugin {

    private Configuration configuration;

    /**
     * Constructor to be used by plugin manager for plugin instantiation.
     * Your plugins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
     *
     * @param wrapper
     */
    protected PluginBase(PluginWrapper wrapper) {
        super(wrapper);
        String propertiesFileName = "plugins/" + getWrapper().getPluginId() + ".properties";
        File propertyFile = new File(propertiesFileName);
        try {

            if (propertyFile.exists() && propertyFile.isFile()) {
                this.configuration = new PropertiesConfiguration(propertiesFileName);
            } else {
                log.error("Cannot log property files : file not found");
                this.configuration = new PropertiesConfiguration();
            }
        } catch (ConfigurationException error) {
            log.error("Cannot log property files :" + error.getMessage(), error);
        }
    }

    protected Configuration getConfiguration() {
        return this.configuration;
    }

}
