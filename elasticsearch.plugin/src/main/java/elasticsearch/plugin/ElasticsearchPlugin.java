package elasticsearch.plugin;

import elasticsearch.plugin.task.store.StoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.RuntimeMode;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.extension.PluginBase;

/**
 * Created by Stephane on 08/01/2015.
 */


public class ElasticsearchPlugin extends PluginBase {

    private static StoreManager storeManager;

    public synchronized static StoreManager getStoreManager() {
        return ElasticsearchPlugin.storeManager;
    }

    public synchronized static void setStoreManager(StoreManager storeManager) {
        ElasticsearchPlugin.storeManager = storeManager;
    }

    protected final Logger logger;

    public ElasticsearchPlugin(PluginWrapper wrapper) {
        super(wrapper);
        logger = LoggerFactory.getLogger(wrapper.getPluginId());
        StoreConfiguration storeConfiguration = new StoreConfiguration(getConfiguration());
        StoreManager storeManager = new StoreManager("storeManager", storeConfiguration);
        ElasticsearchPlugin.setStoreManager(storeManager);

    }

    @Override
    public void start() {
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
            logger.warn("Plugin runs in development mode !!");
        }
        //start ES transport
        try {
            ElasticsearchPlugin.getStoreManager().start();
        } catch (ApplicationException error) {
            logger.error("Fail to start plugin", error);
        }

    }

    @Override
    public void stop() {
        //stop ES transport
        try {
            ElasticsearchPlugin.getStoreManager().stop();
        } catch (ApplicationException error) {
            logger.error("Fail to stop plugin", error);
        }
    }


}
