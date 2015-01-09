package elasticsearch.plugin;

import elasticsearch.plugin.task.store.configuration.Store;
import org.apache.commons.lang.StringUtils;
import ro.fortsoft.pf4j.Extension;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.RuntimeMode;
import stream.machine.core.configuration.task.TaskConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.task.store.ConfigurationStore;

import java.util.List;

/**
 * Created by Stephane on 08/01/2015.
 */


public class ElasticsearchPlugin extends Plugin {

    public ElasticsearchPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
            System.out.println(StringUtils.upperCase("ElasticsearchPlugin"));
        }
        //start ES transport
    }

    @Override
    public void stop() {
        //stop ES transport
    }


}
