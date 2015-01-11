package elasticsearch.plugin;

import org.apache.commons.configuration.Configuration;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 11/01/2015.
 */
public class StoreConfiguration {

    private final String cluster;
    private final boolean embedded;
    private final List<String> nodes;
    private final int retentionPeriod;
    private final List<String> properties;

    public StoreConfiguration(String cluster) {
        this.embedded = true;
        this.cluster = cluster;
        this.nodes = null;
        this.retentionPeriod = 1;
        this.properties = null;
    }

    public StoreConfiguration(Configuration configuration) {
        this.embedded = configuration.getBoolean("elasticsearch.embedded");
        this.cluster = configuration.getString("elasticsearch.cluster");
        this.nodes = configuration.getList("elasticsearch.nodes");
        this.retentionPeriod = configuration.getInt("elasticsearch.retentionPeriod");
        this.properties = configuration.getList("elasticsearch.property");
    }

    public StoreConfiguration(String cluster, boolean embedded, List<String> nodes, int retentionPeriod, List<String> properties) {
        this.cluster = cluster;
        this.embedded = embedded;
        this.nodes = nodes;
        this.retentionPeriod = retentionPeriod;
        this.properties = properties;
    }

    public String getCluster() {
        return cluster;
    }

    public Map<String, String> getProperties() {
        Map<String, String> propertiesDetails = new HashMap<String, String>();
        for (String property : properties) {
            String[] propertyDetail = property.split("=");
            propertiesDetails.put(propertyDetail[0], propertyDetail[1]);
        }
        return propertiesDetails;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public List<InetSocketTransportAddress> getNodes() {
        if (nodes != null && nodes.size() > 0) {
            List<InetSocketTransportAddress> nodeAddresses = new ArrayList<InetSocketTransportAddress>();
            for (String node : nodes) {
                String[] nodeData = node.split(":");
                nodeAddresses.add(new InetSocketTransportAddress(nodeData[0], Integer.parseInt(nodeData[1])));
            }
            return nodeAddresses;
        }
        return new ArrayList<InetSocketTransportAddress>();
    }

    public int getRetentionPeriod() {
        return retentionPeriod;
    }
}
