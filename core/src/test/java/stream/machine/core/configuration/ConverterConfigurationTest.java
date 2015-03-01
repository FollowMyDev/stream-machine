package stream.machine.core.configuration;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

public class ConverterConfigurationTest {
    public static ConverterConfiguration build(String configurationName )
    {
        ImmutableMap.Builder<String,String> conversions = new ImmutableMap.Builder<String,String>();
        conversions.put("d", "Integer");
        conversions.put("e", "Boolean");
        conversions.put("f", "Double");
        conversions.put("g", "String");
        return new ConverterConfiguration(configurationName, conversions.build());
    }

    @Test
    public void testGetFieldsToConvert() throws Exception {
        ConverterConfiguration configuration = build("Test");
        Assert.assertEquals(4, configuration.getFieldsToConvert().size());
    }
}