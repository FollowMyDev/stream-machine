package stream.machine.core.configuration.store;

import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;

import static org.junit.Assert.*;

public class EventStorageConfigurationTest {
    public static EventStorageConfiguration build(String configurationName )
    {
        return new EventStorageConfiguration(configurationName, 5000,1000,500);
    }

    @Test
    public void testGetTimeOutInMilliseconds() throws Exception {
        EventStorageConfiguration configuration = build("Test");
        Assert.assertEquals(5000,configuration.getTimeOutInMilliseconds());
        Assert.assertEquals(1000,configuration.getBulkSize());
        Assert.assertEquals(500,configuration.getBulkPeriodInMilliseconds());
    }


}