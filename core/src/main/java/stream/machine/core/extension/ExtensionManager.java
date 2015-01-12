package stream.machine.core.extension;

import ro.fortsoft.pf4j.*;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.task.store.ConfigurationStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 08/01/2015.
 */
public class ExtensionManager extends ManageableBase {
    private final PluginManager pluginManager = new DefaultPluginManager();
    private final Map<String, PluginClassLoader> extensions;

    public ExtensionManager() {
        super("ExtensionManager");
        pluginManager.loadPlugins();
        this.extensions = new HashMap<String, PluginClassLoader>();
    }

    @Override
    public void start() throws ApplicationException {
        logger.info("Loading plugins ...");
        pluginManager.startPlugins();
        for (PluginWrapper plugin : pluginManager.getPlugins()) {
            logger.info("Found plugin :"+plugin.getPluginId());
            for (String extension : pluginManager.getExtensionClassNames(plugin.getPluginId())) {
                this.extensions.put(extension, plugin.getPluginClassLoader());
            }
        }
        logger.info("... plugin loaded");
    }

    @Override
    public void stop() throws ApplicationException {
        logger.info("Unloaded plugins ...");
        pluginManager.stopPlugins();
        this.extensions.clear();
        logger.info("... plugins unloaded");
    }

    public ConfigurationStore getConfigurationStore(String className) throws ApplicationException {
        logger.debug("Get configuration store : " + className);
        List<ConfigurationStore> configurationStores = pluginManager.getExtensions(ConfigurationStore.class);
        for (ConfigurationStore store : configurationStores) {
            if (store.getClass().getName().equals(className)) {
                return store;
            }
        }
        logger.info("The configuration store cannot be found");
        throw new ApplicationException("The configuration store cannot be found");
    }
}
