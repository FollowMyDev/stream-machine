package elasticsearch.plugin;


import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;


public abstract class ElasticsearchTestBase {

    private EmbeddedElasticsearch embeddedElasticsearchServer;

    @Before
    public void startEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer = new EmbeddedElasticsearch();
    }

    @After
    public void shutdownEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer.shutdown();
    }

    /**
     * By using this method you can access the embedded server.
     */
    protected Client getClient() {
        return embeddedElasticsearchServer.getClient();
    }
}
