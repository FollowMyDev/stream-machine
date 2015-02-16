package elasticsearch.plugin.task.store.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import elasticsearch.plugin.ElasticsearchPlugin;
import elasticsearch.plugin.task.store.StoreBase;
import elasticsearch.plugin.task.store.StoreManager;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConfigurationType;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.ConfigurationStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Stephane on 08/01/2015.
 */

@Extension
public class Store extends StoreBase implements ConfigurationStore {

    private final static String index = "configuration";
    private final ObjectMapper mapper;

    public Store(StoreManager storeManager) {
        super("Elasticsearch.ConfigurationStore", storeManager);
        this.mapper = new ObjectMapper();
    }

    public Store() {
        super("Elasticsearch.ConfigurationStore", ElasticsearchPlugin.getStoreManager());
        this.mapper = new ObjectMapper();
    }

    @Override
    public <T extends Configuration> List<T> readAll(ConfigurationType type, Class<T> configurationClass) {

        Client client = storeManager.getClient();
        if (client == null) return null;
        List<T> configurations = new ArrayList<T>();

        boolean searchComplete = false;
        QueryBuilder typeQuery = QueryBuilders.matchQuery("type", type);

        SearchResponse searchResponse = client.prepareSearch()
                .setIndices(this.index)
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setQuery(typeQuery)
                .setSize(100).execute().actionGet();
        while (!searchComplete) {
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                try {
                    configurations.add(mapper.readValue(hit.getSourceAsString(), configurationClass));
                } catch (IOException error) {
                    logger.error("Failed to serialize configuration", error);
                }
            }
            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(600000))
                    .execute()
                    .actionGet();
            //Break condition: No hits are returned
            if (searchResponse.getHits().getHits().length == 0) {
                searchComplete = true;
            }
        }

        return configurations;
    }

    @Override
    public <T extends Configuration> T readConfiguration(String name, ConfigurationType type, Class<T> configurationClass) {
        Client client = storeManager.getClient();
        if (client == null) return null;
        GetResponse response = client.prepareGet(this.index, type.toString(), name)
                .execute()
                .actionGet();

        try {
            if (response.isExists()) {
                return mapper.readValue(response.getSourceAsString(), configurationClass);
            }
        } catch (IOException error) {
            logger.error("Failed to serialize configuration", error);
        }
        return null;
    }

    @Override
    public <T extends Configuration> void saveConfiguration(T configuration) throws ApplicationException {
        Client client = storeManager.getClient();
        if (client == null) return;
        IndexRequestBuilder indexRequest;

        try {
            String configurationAsString = mapper.writeValueAsString(configuration);
            indexRequest = client.prepareIndex(this.index, configuration.getType().toString(), configuration.getName())
                    .setSource(configurationAsString);
        } catch (JsonProcessingException error) {
            logger.error(String.format("Json conversion failed for configuration %s", configuration.getName()), error);
            return;
        }

        IndexResponse indexResponse = indexRequest.execute().actionGet();
        logger.info(String.format("Configuration %s saved with version %s", configuration.getName(), indexResponse.getVersion()));
    }

    @Override
    public <T extends Configuration> void updateConfiguration(T configuration) throws ApplicationException {
        Client client = storeManager.getClient();
        if (client == null) return;
        try {
            String configurationAsString = mapper.writeValueAsString(configuration);
            UpdateRequest updateRequest = new UpdateRequest(this.index, configuration.getType().toString(), configuration.getName())
                    .doc(configurationAsString);
            long version = client.update(updateRequest).get().getVersion();
        } catch (JsonProcessingException error) {
            logger.error(String.format("Json conversion failed for configuration %s", configuration.getName()), error);
            return;
        } catch (InterruptedException error) {
            logger.error(String.format("Update failed for configuration %s", configuration.getName(), error));
            return;
        } catch (ExecutionException error) {
            logger.error(String.format("Update failed for configuration %s", configuration.getName(), error));
            return;
        }
        logger.info(String.format("Configuration %s was successfully updated", configuration.getName()));
    }

    @Override
    public <T extends Configuration> void deleteConfiguration(T configuration) throws ApplicationException {
        Client client = storeManager.getClient();
        if (client == null) return;
        DeleteResponse response = client.prepareDelete(this.index, configuration.getType().toString(), configuration.getName())
                .execute()
                .actionGet();
        if (response.isFound()) {
            logger.info(String.format("Configuration %s is deleted", configuration.getName()));
        }
        else {
            logger.info(String.format("Configuration %s was not found", configuration.getName()));
        }
    }

    @Override
    public void start() throws ApplicationException {
        buildIndex(this.index);
    }

    @Override
    public void stop() throws ApplicationException {

    }


}
