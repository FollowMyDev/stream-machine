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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import ro.fortsoft.pf4j.Extension;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.store.ConfigurationStore;
import stream.machine.core.task.TaskType;

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
    private final static String type = "configurationData";
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
    public List<Configuration> readAll(TaskType type) throws ApplicationException{

        Client client = storeManager.getClient();
        if (client == null) return null;
        List<Configuration> configurations = new ArrayList<Configuration>();

        boolean searchComplete = false;
        QueryBuilder typeQuery = QueryBuilders.matchQuery("type", type);

        SearchResponse searchResponse = client.prepareSearch()
                .setIndices(Store.index)
                .setTypes(Store.type)
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setQuery(typeQuery)
                .setSize(100).execute().actionGet();
        while (!searchComplete) {
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                try {
                    configurations.add(mapper.readValue(hit.getSourceAsString(), Configuration.class));
                } catch (IOException error) {
                    throw new ApplicationException("Failed to serialize configuration", error);
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
    public Configuration readConfiguration(String name) throws ApplicationException{
        Client client = storeManager.getClient();
        if (client == null) return null;
        GetResponse response = client.prepareGet(Store.index, Store.type, name)
                .execute()
                .actionGet();

        try {
            if (response.isExists()) {
                return mapper.readValue(response.getSourceAsString(), Configuration.class);
            }
        } catch (IOException error) {
            throw new ApplicationException("Failed to serialize configuration", error);
        }
        return null;
    }

    @Override
    public void saveConfiguration(Configuration configuration) throws ApplicationException {
        Client client = storeManager.getClient();
        if (client == null) return;
        IndexRequestBuilder indexRequest;

        try {
            String configurationAsString = mapper.writeValueAsString(configuration);
            indexRequest = client.prepareIndex(Store.index, Store.type, configuration.getName())
                    .setSource(configurationAsString);
        } catch (JsonProcessingException error) {
            throw new ApplicationException(String.format("Json conversion failed for configuration %s", configuration.getName()), error);
        }

        IndexResponse indexResponse = indexRequest.execute().actionGet();
        logger.info(String.format("Configuration %s saved with version %s", configuration.getName(), indexResponse.getVersion()));
    }

    @Override
    public void updateConfiguration(Configuration configuration) throws ApplicationException {
        Client client = storeManager.getClient();
        if (client == null) return;
        try {
            String configurationAsString = mapper.writeValueAsString(configuration);
            UpdateRequest updateRequest = new UpdateRequest(Store.index, Store.type, configuration.getName())
                    .doc(configurationAsString);
            long version = client.update(updateRequest).get().getVersion();
        } catch (JsonProcessingException error) {
            throw new ApplicationException(String.format("Json conversion failed for configuration %s", configuration.getName()), error);
        } catch (InterruptedException error) {
            throw new ApplicationException(String.format("Update failed for configuration %s", configuration.getName(), error));
        } catch (ExecutionException error) {
            throw new ApplicationException(String.format("Update failed for configuration %s", configuration.getName(), error));
        }
        logger.info(String.format("Configuration %s was successfully updated", configuration.getName()));
    }

    @Override
    public void deleteConfiguration(String name) throws ApplicationException {
        Client client = storeManager.getClient();
        if (client == null) return;
        DeleteResponse response = client.prepareDelete(Store.index, Store.type, name)
                .execute()
                .actionGet();
        if (response.isFound()) {
            logger.info(String.format("Configuration %s is deleted", name));
        }
        else {
            logger.info(String.format("Configuration %s was not found", name));
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
