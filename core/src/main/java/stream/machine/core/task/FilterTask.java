package stream.machine.core.task;

import stream.machine.core.configuration.Configuration;
import stream.machine.core.configuration.FilterConfiguration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Stephane on 27/02/2015.
 */
public class FilterTask extends TaskBase {
    private final List<String> fieldsToFilter;

    public FilterTask(Configuration configuration, ExecutorService executor) {
        super(configuration, executor);
        FilterConfiguration filterConfiguration = new FilterConfiguration(configuration);
        this.fieldsToFilter = filterConfiguration.getFieldsToFilter();
    }

    @Override
    protected boolean canProcess() {
        return (fieldsToFilter!= null);
    }

    @Override
    protected Event doProcess(Event event) {
        if (event == null) return event;
        for (String field : fieldsToFilter) {
            if (field != null) {
                if (event.containsKey(field)) {
                    event.remove(field);
                }
            }
        }
        return event;
    }

    @Override
    public String getErrorField() {
        return "filterError";
    }

    @Override
    public void start() throws ApplicationException {

    }

    @Override
    public void stop() throws ApplicationException {

    }




}
