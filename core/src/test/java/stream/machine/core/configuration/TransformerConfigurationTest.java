package stream.machine.core.configuration;

import junit.framework.Assert;
import org.junit.Test;

public class TransformerConfigurationTest {
    public static TransformerConfiguration build(String configurationName )
    {
        StringBuilder template = new StringBuilder("");
        template.append("#macro( put $key $value )");
        template.append("#${event.put($key,$value)}");
        template.append("#end");
        template.append(" ");
        template.append("#macro( sum $keyA $keyB $keyC )");
        template.append("#set( $valueA = $event.get($keyA) )");
        template.append("#set( $valueB = $event.get($keyB) )");
        template.append("#set( $valueC = $valueA+$valueB )");
        template.append("#put( $keyC $valueC )");
        template.append("#end");
        template.append(" ");
        template.append("#sum( \"a\" \"b\" \"c\")");

        return new TransformerConfiguration(configurationName, template.toString());

    }

    @Test
    public void testGetTemplate() throws Exception {
        TransformerConfiguration configuration = build("Test");
        Assert.assertNotNull(configuration.getTemplate());
    }


}