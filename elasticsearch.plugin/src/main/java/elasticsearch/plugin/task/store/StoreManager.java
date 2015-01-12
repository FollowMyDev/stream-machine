package elasticsearch.plugin.task.store;

import elasticsearch.plugin.StoreConfiguration;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;


public class StoreManager extends ManageableBase {

    private final StoreConfiguration configuration;
    private Client client;


    public StoreManager(String name, StoreConfiguration configuration) {
        super(name);
        this.configuration = configuration;
    }

    public StoreManager(String name, Client client) {
        super(name);
        this.configuration = null;
        this.client = client;
    }

    @Override
    public void start() throws ApplicationException {
        if (configuration == null) return;
        if (configuration.isEmbedded()) {
            ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
            try {
                settings.put("node.name", "worker-" + InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException error) {
                settings.put("node.name", "worker-" + UUID.randomUUID().toString());
            }

            for (Map.Entry<String, String> property : configuration.getProperties().entrySet()) {
                settings.put(property.getKey(), property.getValue());
            }

            Node node = NodeBuilder.nodeBuilder().settings(settings).clusterName(configuration.getCluster()).data(true).local(false).node();
            this.client = node.client();
        } else {
            Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", configuration.getCluster()).build();
            TransportClient transportClient = new TransportClient(settings);
            for (InetSocketTransportAddress node : configuration.getNodes()) {
                transportClient.addTransportAddress(node);
            }
            this.client = transportClient;
        }
    }

    @Override
    public void stop() throws ApplicationException {
        if (configuration == null) return;
        this.client.close();
    }

    public Client getClient() {
        return client;
    }


    public void createIndex(String index) throws ApplicationException {
        CreateIndexResponse response = client.admin().indices().create(new CreateIndexRequest(index)).actionGet();
        if (!response.isAcknowledged()) {
            throw new ApplicationException("Index creation failed !");
        }
    }

    public void deleteIndex(String index) throws ApplicationException {
        try {
            DeleteIndexResponse response = client.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
            if (!response.isAcknowledged()) {
                throw new ApplicationException("Index deletion failed !");
            }
        } catch (Exception error) {
            logger.error("Index deletion failed !", error);
            throw new ApplicationException("Index deletion failed !", error);
        }
    }

    public boolean isIndexExist(String index) {
        ActionFuture<IndicesExistsResponse> exists = client.admin().indices()
                .exists(new IndicesExistsRequest(index));
        IndicesExistsResponse response = exists.actionGet();
        return (response != null) ? response.isExists() : false;
    }

}