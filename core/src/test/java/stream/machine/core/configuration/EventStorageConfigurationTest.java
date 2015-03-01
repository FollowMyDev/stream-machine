package stream.machine.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class EventStorageConfigurationTest {
    public static StorageConfiguration build(String configurationName )
    {
        return new StorageConfiguration(configurationName, 5000,1000,500);
    }

    @Test
    public void testGetTimeOutInMilliseconds() throws Exception {
        StorageConfiguration configuration = build("Test");
        Assert.assertEquals(5000,configuration.getTimeOutInMilliseconds());
        Assert.assertEquals(1000,configuration.getBulkSize());
        Assert.assertEquals(500,configuration.getBulkPeriodInMilliseconds());
    }


    @Test
    public void testSerialize() throws Exception {
        StorageConfiguration configuration = build("Test");
        ObjectMapper mapper = new ObjectMapper();
        String configurationAsString = mapper.writeValueAsString(configuration);
        Assert.assertTrue(configurationAsString.contains("5000"));
        Assert.assertTrue(configurationAsString.contains("1000"));
        Assert.assertTrue(configurationAsString.contains("500"));
    }

}