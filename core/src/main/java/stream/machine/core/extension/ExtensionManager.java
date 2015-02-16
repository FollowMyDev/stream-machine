package stream.machine.core.extension;

import ro.fortsoft.pf4j.*;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.store.EventStore;
import stream.machine.core.store.Store;
import stream.machine.core.store.StoreManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 08/01/2015.
 */
public class ExtensionManager extends ManageableBase implements StoreManager {
    private final PluginManager pluginManager = new DefaultPluginManager();
    private final Map<String, PluginClassLoader> extensions;
    private final String configurationStoreClassName;
    private final String eventStoreClassName;

    public ExtensionManager(String configurationStoreClassName, String eventStoreClassName) {
        super("ExtensionManager");
        this.configurationStoreClassName = configurationStoreClassName;
        this.eventStoreClassName = eventStoreClassName;
        pluginManager.loadPlugins();
        this.extensions = new HashMap<String, PluginClassLoader>();
    }

    @Override
    public void start() throws ApplicationException {
        logger.info("Loading plugins ...");
        pluginManager.startPlugins();
        for (PluginWrapper plugin : pluginManager.getPlugins()) {
            logger.info("Found plugin :" + plugin.getPluginId());
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

    @Override
    public ConfigurationStore getConfigurationStore() {
        return getStore(this.configurationStoreClassName, ConfigurationStore.class);
    }

    @Override
    public EventStore getEventStore() {
        return getStore(this.eventStoreClassName, EventStore.class);
    }

    public <T extends Store> T getStore(String className, Class storeClass) {
        logger.debug(String.format("Get store : %s", storeClass));
        List<T> stores = pluginManager.getExtensions(storeClass);
        for (T store : stores) {
            if (store.getClass().getName().equals(className)) {
                return store;
            }
        }
        logger.info(String.format("The store with name %s cannot be found", className));
        return null;
    }
}
