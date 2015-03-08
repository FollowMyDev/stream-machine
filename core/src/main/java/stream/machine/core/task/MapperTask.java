package stream.machine.core.task;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.MapperConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by Stephane on 27/02/2015.
 */
public class MapperTask extends TaskBase {
    private final Map<String, String> fieldsToMap;

    public MapperTask(Configuration configuration, ExecutorService executor) {
        super(configuration, executor);
        MapperConfiguration mapperConfiguration = new MapperConfiguration(configuration);
        this.fieldsToMap = mapperConfiguration.getFieldsToMap();
    }

    @Override
    protected boolean canProcess() {
        return (fieldsToMap != null);
    }

    @Override
    protected Event doProcess(Event event) {
        if (event == null) return event;
        for (Map.Entry<String, String> mapping : fieldsToMap.entrySet()) {
            if (mapping != null && mapping.getKey() != null && mapping.getValue() != null) {
                String sourceKey = mapping.getKey();
                if (event.containsKey(sourceKey)) {
                    Object value = event.get(sourceKey);
                    String destinationKey = mapping.getValue();
                    event.put(destinationKey, value);
                }
            }
        }
        return event;
    }

    @Override
    public String getErrorField() {
        return "mapperError";
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }


}


