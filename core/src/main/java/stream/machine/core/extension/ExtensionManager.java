package stream.machine.core.extension;

import ro.fortsoft.pf4j.*;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.store.ConfigurationStore;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 08/01/2015.
 */
public class ExtensionManager {
    private final PluginManager pluginManager = new DefaultPluginManager();
    private final Map<String, PluginClassLoader> extensions;

    public ExtensionManager() {
        pluginManager.loadPlugins();
        this.extensions = new HashMap<String, PluginClassLoader>();
    }

    public void load() throws ApplicationException {
        pluginManager.startPlugins();

        for (PluginWrapper plugin : pluginManager.getPlugins()) {
            for (String extension : pluginManager.getExtensionClassNames(plugin.getPluginId())) {
                this.extensions.put(extension, plugin.getPluginClassLoader());
            }
        }

    }

    public void unload() throws ApplicationException {
        pluginManager.stopPlugins();
        this.extensions.clear();
    }

    public ConfigurationStore getConfigurationStore(String className) throws ApplicationException {


        List<ConfigurationStore> configurationStores = pluginManager.getExtensions(ConfigurationStore.class);
        for (ConfigurationStore store : configurationStores) {
            if (store.getClass().getName().equals(className)) {
                return store;
            }
        }

        throw new ApplicationException("The configuration store cannot be found");
    }
}
