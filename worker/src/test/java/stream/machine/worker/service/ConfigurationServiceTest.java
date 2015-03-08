package stream.machine.worker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import io.dropwizard.testing.junit.DropwizardAppRule;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConverterConfiguration;
import stream.machine.core.task.ConverterTask;
import stream.machine.core.task.TaskType;
import stream.machine.worker.ServiceTest;
import stream.machine.worker.Worker;
import stream.machine.worker.configuration.WorkerConfiguration;

import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

public class ConfigurationServiceTest extends ServiceTest {

    @ClassRule
    public static final DropwizardAppRule<WorkerConfiguration> RULE = new DropwizardAppRule<WorkerConfiguration>(Worker.class, ("src/test/resources/configuration.yaml"));

    @Before
    public void setUp() {
        RestAssured.port = RULE.getLocalPort();
        RestAssured.defaultParser = Parser.JSON;
    }

    public ConfigurationServiceTest() {
        super("ConfigurationServiceTest");
    }

    protected String buildConverterConfiguration(String configurationName) {
        try {
            ImmutableMap.Builder<String, String> conversions = new ImmutableMap.Builder<String, String>();
            conversions.put("d", "Integer");
            conversions.put("e", "Boolean");
            conversions.put("f", "Double");
            conversions.put("g", "String");
            Configuration configuration = new ConverterConfiguration(configurationName, conversions.build());
            return mapper.writeValueAsString(configuration);
        } catch (JsonProcessingException error) {
            logger.error("Configuration creation failed", error);
        }
        return null;
    }

    @Test
    public void testReadAllEventTransformerConfiguration() throws Exception {
        testSaveConfiguration();

        List configurations = RestAssured.
                get("/configuration/readAll/Convert").
                as(List.class);

        Assert.assertEquals(1,configurations.size());

        ConverterConfiguration converterConfiguration = new ConverterConfiguration();
        converterConfiguration.putAll((Map<String,Object>)configurations.get(0) );
        Assert.assertEquals(TaskType.Convert,converterConfiguration.getType());
    }

    @Test
    public void testReadConfigurationEventTransformerConfiguration() throws Exception {
        testSaveConfiguration();

        Map configuration = RestAssured.
                get("/configuration/read/ConverterTask").
                as(Map.class);

        Assert.assertNotNull(configuration);

        ConverterConfiguration converterConfiguration = new ConverterConfiguration();
        converterConfiguration.putAll(configuration);
        Assert.assertEquals(TaskType.Convert,converterConfiguration.getType());
    }

    @Test
    public void testSaveConfiguration() throws Exception {
        RestAssured.
        given().
            contentType(MediaType.APPLICATION_JSON).
            body(buildConverterConfiguration("ConverterTask")).
        when().
            post("/configuration/save").
        then().
            assertThat().statusCode(204);
    }

    @Test
    public void testUpdateConfiguration() throws Exception {
        testSaveConfiguration();

        Map configuration = RestAssured.
                get("/configuration/read/ConverterTask").
                as(Map.class);

        Assert.assertNotNull(configuration);


        ConverterConfiguration converterConfiguration = new ConverterConfiguration();
        converterConfiguration.putAll(configuration);
        Assert.assertEquals(4,converterConfiguration.getFieldsToConvert().size());
        converterConfiguration.getFieldsToConvert().put("z", ConverterTask.SupportedType.Double.toString());

        RestAssured.
                given().
                contentType(MediaType.APPLICATION_JSON).
                body(mapper.writeValueAsString(configuration)).
                when().
                post("/configuration/update").
                then().
                assertThat().statusCode(204);

        configuration = RestAssured.
                get("/configuration/read/ConverterTask").
                as(Map.class);

        Assert.assertNotNull(configuration);

        converterConfiguration = new ConverterConfiguration();
        converterConfiguration.putAll(configuration);
        Assert.assertEquals(5,converterConfiguration.getFieldsToConvert().size());
    }

    @Test
    public void testDeleteConfiguration() throws Exception {
        testSaveConfiguration();


        RestAssured.
                given().
                contentType(MediaType.APPLICATION_JSON).
                when().
                delete("/configuration/delete/ConverterTask").
                then().
                assertThat().statusCode(204);

        try {
            Map configuration = RestAssured.
                    get("/configuration/read/ConverterTask").
                    as(Map.class);
        }
        catch (Exception error) {
            Assert.assertTrue(error.getMessage().contains("No content to map due to end-of-input"));
            return;
        }

        Assert.fail("Configuration stil exists");

    }
}