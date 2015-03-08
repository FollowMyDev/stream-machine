package stream.machine.core.task;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.UserAgentParserConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stephane on 27/02/2015.
 */
public class UserAgentParserTask extends TaskBase {
    private final String userAgentField;
    private final UserAgentStringParser parser;
    private final Cache<String, ReadableUserAgent> cache;

    public UserAgentParserTask(Configuration configuration, ExecutorService executor) {
        super(configuration, executor);
        UserAgentParserConfiguration userAgentParserConfiguration = new UserAgentParserConfiguration(configuration);
        this.userAgentField = userAgentParserConfiguration.getUserAgentField();
        this.parser = UADetectorServiceFactory.getCachingAndUpdatingParser();
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build();
    }

    @Override
    protected boolean canProcess() {
        return (userAgentField != null);
    }

    @Override
    protected Event doProcess(Event event) {
        if (event == null) return event;
        if (event.containsKey(userAgentField)) {
            Object value = event.get(userAgentField);
            if (value != null) {
                String userAgentString = value.toString();
                ReadableUserAgent result = cache.getIfPresent(userAgentString);
                if (result == null) {
                    result = parser.parse(userAgentString);
                    cache.put(userAgentString, result);
                }
                event.put("userAgentDeviceCategory", result.getDeviceCategory().getCategory());
                event.put("userAgentName", result.getName());
                event.put("userAgentOperatingSystem", result.getOperatingSystem().getName());
                event.put("userAgentOperatingSystemVersion", result.getOperatingSystem().getVersionNumber().toVersionString());
                event.put("userAgentProducer", result.getProducer());
                event.put("userAgentType", result.getType().getName());
                event.put("userAgentVersion", result.getVersionNumber().toVersionString());
            }
        }
        return event;
    }

    @Override
    public String getErrorField() {
        return "userAgentParserError";
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }


}
