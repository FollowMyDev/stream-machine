package stream.machine.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConverterConfiguration;
import stream.machine.worker.configuration.WorkerConfiguration;

/**
 * Created by Stephane on 08/03/2015.
 */
public abstract class ServiceTest {
    protected final ObjectMapper mapper;
    protected Logger logger;



    protected ServiceTest(String name) {
        mapper = new ObjectMapper();
        logger = LoggerFactory.getLogger(name);
    }



}
