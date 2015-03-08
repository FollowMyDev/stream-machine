package stream.machine.core.task;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.ConverterConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by Stephane on 27/02/2015.
 */
public class ConverterTask extends TaskBase {
    private final Map<String, SupportedType> fieldsToConvert;

    public enum SupportedType {
        Integer,
        Double,
        String,
        Boolean
    }

    public ConverterTask(Configuration configuration, ExecutorService executor) {
        super(configuration, executor);
        ConverterConfiguration converterConfiguration = new ConverterConfiguration(configuration);
        this.fieldsToConvert = new ConcurrentHashMap<String, SupportedType>();
        if (converterConfiguration.getFieldsToConvert() != null) {
            for (Map.Entry<String, String> fieldToConvert : converterConfiguration.getFieldsToConvert().entrySet()) {
                if (fieldToConvert != null && fieldToConvert.getKey() != null && fieldToConvert.getValue() != null) {
                    try {
                        this.fieldsToConvert.put(fieldToConvert.getKey(), SupportedType.valueOf(fieldToConvert.getValue()));
                    } catch (Exception error) {
                        logger.error("Field conversion is not valid", error);
                    }
                }
            }
        }
    }

    @Override
    protected boolean canProcess() {
        return  (this.fieldsToConvert != null);
    }

    @Override
    protected Event doProcess(Event event) {
        if (event == null ) return event;
        for (Map.Entry<String, SupportedType> field : fieldsToConvert.entrySet()) {
            if (field != null && field.getKey() != null && field.getValue() != null) {
                String fieldKey =  field.getKey();
                if ( event.containsKey(fieldKey)) {
                    Object value = event.get(fieldKey);
                    if ( value != null ) {
                        switch (field.getValue()) {
                            case Integer:
                                event.put(fieldKey,Integer.parseInt(value.toString()));
                                break;
                            case Double:
                                event.put(fieldKey,Double.parseDouble(value.toString()));
                                break;
                            case String:
                                event.put(fieldKey,value.toString());
                                break;
                            case Boolean:
                                event.put(fieldKey,Boolean.parseBoolean(value.toString()));
                                break;
                        }
                    }
                }
            }
        }
        return event;
    }


    @Override
    public String getErrorField() {
        return "converterError";
    }


    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }




}
