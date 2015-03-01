package elasticsearch.plugin;

import elasticsearch.plugin.task.store.StoreManager;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.RuntimeMode;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.extension.PluginBase;

/**
 * Created by Stephane on 08/01/2015.
 */


public class ElasticsearchPlugin extends PluginBase {

    //Todo: add logging
    private static StoreManager storeManager;
    public synchronized static StoreManager getStoreManager() {
        return ElasticsearchPlugin.storeManager;
    }
    private synchronized static void setStoreManager(StoreManager storeManager) {
        ElasticsearchPlugin.storeManager = storeManager;
    }

    private static boolean useDateInIndex;
    public synchronized static boolean useDateInIndex() {
        return ElasticsearchPlugin.useDateInIndex;
    }
    private synchronized static void useDateInIndex(boolean useDateInIndex) {
        ElasticsearchPlugin.useDateInIndex = useDateInIndex;
    }

    private static boolean useEventTypeInIndex;
    public synchronized static boolean useEventTypeInIndex() {
        return ElasticsearchPlugin.useEventTypeInIndex;
    }
    private synchronized static void useEventTypeInIndex(boolean useEventTypeInIndex) {
        ElasticsearchPlugin.useEventTypeInIndex = useEventTypeInIndex;
    }

    private static String indexPattern;
    public synchronized static String getIndexPattern() {
        return ElasticsearchPlugin.indexPattern;
    }
    private synchronized static void setIndexPattern(String indexPattern) {
        ElasticsearchPlugin.indexPattern = indexPattern;
    }

   // protected final Logger logger;

    public ElasticsearchPlugin(PluginWrapper wrapper) {
        super(wrapper);
        StoreConfiguration storeConfiguration = new StoreConfiguration(getConfiguration());
        StoreManager storeManager = new StoreManager(storeConfiguration);
        ElasticsearchPlugin.setStoreManager(storeManager);
        ElasticsearchPlugin.useDateInIndex(storeConfiguration.useDateInIndex());
        ElasticsearchPlugin.useEventTypeInIndex(storeConfiguration.useEventTypeInIndex());
        ElasticsearchPlugin.setIndexPattern(storeConfiguration.getIndexPattern());
    }

    @Override
    public void start() {
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
          //logger.warn("Plugin runs in development mode !!");
        }
        //start ES transport
        try {
            ElasticsearchPlugin.getStoreManager().start();
        } catch (ApplicationException error) {
           //logger.error("Fail to start plugin", error);
        }

    }

    @Override
    public void stop() {
        //stop ES transport
        try {
            ElasticsearchPlugin.getStoreManager().stop();
        } catch (ApplicationException error) {
           // logger.error("Fail to stop plugin", error);
        }
    }


}
