package stream.machine.core.configuration.sequence;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.store.EventStorageConfigurationTest;
import stream.machine.core.configuration.transform.EventTransformerConfiguration;
import stream.machine.core.configuration.transform.EventTransformerConfigurationTest;

public class SequenceConfigurationTest {
    public static SequenceConfiguration build(String configurationName )
    {
        ImmutableMap.Builder<Integer,Configuration> taskConfigurations = new ImmutableBiMap.Builder<Integer, Configuration>();
        taskConfigurations.put(0, EventTransformerConfigurationTest.build("TaskA"));
        taskConfigurations.put(1, EventStorageConfigurationTest.build("TaskB"));
        return new SequenceConfiguration(configurationName, taskConfigurations.build());
    }

    @Test
    public void testSequenceCount() throws Exception {
        SequenceConfiguration configuration = build("Test");
        Assert.assertEquals(2, configuration.getTaskConfigurations().size());
    }
}