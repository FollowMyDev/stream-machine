package elasticsearch.plugin;

import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

public class StoreConfigurationTest {

    @Test
    public void testConfiguration() throws Exception {
        Configuration configuration = new PropertiesConfiguration("elasticsearch-plugin.properties");
        StoreConfiguration storeConfiguration = new StoreConfiguration(configuration);
        Assert.assertEquals("elasticsearch",storeConfiguration.getCluster());
        Assert.assertEquals(false,storeConfiguration.isEmbedded());
        Assert.assertEquals(1,storeConfiguration.getNodes().size());
        Assert.assertEquals(9300,storeConfiguration.getNodes().get(0).address().getPort());
        Assert.assertEquals(10,storeConfiguration.getRetentionPeriod());
        Assert.assertEquals(3,storeConfiguration.getProperties().size());
        Assert.assertEquals("1",storeConfiguration.getProperties().get("a"));
        Assert.assertEquals("2",storeConfiguration.getProperties().get("b"));
        Assert.assertEquals("3",storeConfiguration.getProperties().get("c"));
    }
}