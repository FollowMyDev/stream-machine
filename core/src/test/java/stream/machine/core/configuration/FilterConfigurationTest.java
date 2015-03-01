package stream.machine.core.configuration;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

public class FilterConfigurationTest {

    public static FilterConfiguration build(String configurationName) {
        ImmutableList.Builder<String> filters = new ImmutableList.Builder<String>();
        filters.add("h");
        filters.add("i");
        filters.add("j");
        filters.add("k");
        return new FilterConfiguration(configurationName, filters.build());
    }

    @Test
    public void testGetFieldsToFilter() throws Exception {
        FilterConfiguration configuration = build("Test");
        Assert.assertEquals(4, configuration.getFieldsToFilter().size());
    }
}