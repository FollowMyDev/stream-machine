package stream.machine.worker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import stream.machine.core.configuration.*;
import stream.machine.core.model.Event;
import stream.machine.worker.ServiceTest;
import stream.machine.worker.Worker;
import stream.machine.worker.configuration.WorkerConfiguration;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;

public class EventServiceTest extends ServiceTest{

    @ClassRule
    public static final DropwizardAppRule<WorkerConfiguration> RULE = new DropwizardAppRule<WorkerConfiguration>(Worker.class, ("src/test/resources/event.yaml"));

    @Before
    public void setUp() {
        RestAssured.port = RULE.getLocalPort();
        RestAssured.defaultParser = Parser.JSON;
    }

    public EventServiceTest() {
        super("EventServiceTest");
    }

    protected String agentParserConfiguration(String configurationName) {
        try {
            Configuration configuration = new UserAgentParserConfiguration(configurationName, "UserAgent");
            return mapper.writeValueAsString(configuration);
        } catch (JsonProcessingException error) {
            logger.error("Configuration creation failed", error);
        }
        return null;
    }

    protected String storeConfiguration(String configurationName) {
        try {
            Configuration configuration = new StorageConfiguration(configurationName, 5000,1000,500);
            return mapper.writeValueAsString(configuration);
        } catch (JsonProcessingException error) {
            logger.error("Configuration creation failed", error);
        }
        return null;
    }


    protected String buildSequenceConfiguration(String configurationName, String taskA, String taskB) {
        try {
            ImmutableMap.Builder<Integer, String> taskConfigurations = new ImmutableMap.Builder<Integer, String>();
            taskConfigurations.put(0, taskA);
            taskConfigurations.put(1, taskB);
            Configuration configuration = new SequenceConfiguration(configurationName, taskConfigurations.build());
            return mapper.writeValueAsString(configuration);
        } catch (JsonProcessingException error) {
            logger.error("Configuration creation failed", error);
        }
        return null;
    }
    @Test
    public void testProcess() throws Exception {
        RestAssured.
                given().
                contentType(MediaType.APPLICATION_JSON).
                body(agentParserConfiguration("Parse")).
                when().
                post("/configuration/save").
                then().
                assertThat().statusCode(204);

        RestAssured.
                given().
                contentType(MediaType.APPLICATION_JSON).
                body(storeConfiguration("Store")).
                when().
                post("/configuration/save").
                then().
                assertThat().statusCode(204);

        RestAssured.
                given().
                contentType(MediaType.APPLICATION_JSON).
                body(buildSequenceConfiguration("ParseAndStore","Parse","Store")).
                when().
                post("/configuration/save").
                then().
                assertThat().statusCode(204);

        Thread.sleep(5000);

        Event event =  new Event("Users.Current","serviceEvent");
        event.put("UserAgent","Mozilla 5.0 (Windows NT 6.1; WOW64) AppleWebKit 537.36 (KHTML, like Gecko) Chrome 29.0.1547.57 Safari 537.36");
        RestAssured.
                given().
                contentType(MediaType.APPLICATION_JSON).
                body(mapper.writeValueAsString(event)).
                when().
                post("/event/process/ParseAndStore").
                then().
                assertThat().statusCode(200);



    }

    @Test
    public void testProcessMultiple() throws Exception {

    }
}