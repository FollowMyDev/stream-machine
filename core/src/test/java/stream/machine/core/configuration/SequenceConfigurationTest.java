package stream.machine.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

public class SequenceConfigurationTest {
    public static SequenceConfiguration build(String configurationName) {
        ImmutableMap.Builder<Integer, String> taskConfigurations = new ImmutableMap.Builder<Integer, String>();
        taskConfigurations.put(0, "TaskA");
        taskConfigurations.put(1, "TaskB");
        return new SequenceConfiguration(configurationName, taskConfigurations.build());
    }

    @Test
    public void testSequenceCount() throws Exception {
        SequenceConfiguration configuration = build("Test");
        Assert.assertEquals(2, configuration.getTaskConfigurations().size());
    }

    @Test
    public void testSerialize() throws Exception {
        SequenceConfiguration configuration = build("Test");
        ObjectMapper mapper = new ObjectMapper();
        String configurationAsString = mapper.writeValueAsString(configuration);
        Assert.assertTrue(configurationAsString.contains("TaskA"));
        Assert.assertTrue(configurationAsString.contains("TaskB"));
    }
}