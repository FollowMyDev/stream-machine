package stream.machine.core.configuration;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapperConfigurationTest {
    public static MapperConfiguration build(String configurationName )
    {
        ImmutableMap.Builder<String,String> mappings = new ImmutableMap.Builder<String,String>();
        mappings.put("d", "h");
        mappings.put("e", "i");
        mappings.put("f", "j");
        mappings.put("g", "k");
        return new MapperConfiguration(configurationName, mappings.build());
    }

    @Test
    public void testGetFieldsToConvert() throws Exception {
        MapperConfiguration configuration = build("Test");
        Assert.assertEquals(4, configuration.getFieldsToMap().size());
    }

}