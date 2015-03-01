package stream.machine.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAgentParserConfigurationTest {

    public static UserAgentParserConfiguration build(String configurationName )
    {
        return new UserAgentParserConfiguration(configurationName, "UserAgent");
    }
    @Test
    public void testGetUserAgentField() throws Exception {
        UserAgentParserConfiguration configuration = build("Test");
        Assert.assertEquals("UserAgent", configuration.getUserAgentField());
    }

    @Test
    public void testSerialize() throws Exception {
        UserAgentParserConfiguration configuration = build("Test");
        ObjectMapper mapper = new ObjectMapper();
        String configurationAsString = mapper.writeValueAsString(configuration);
        Assert.assertTrue(configurationAsString.contains("UserAgent"));
    }
}