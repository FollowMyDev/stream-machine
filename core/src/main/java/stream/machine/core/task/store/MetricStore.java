package stream.machine.core.task.store;

import stream.machine.core.model.Metric;
import stream.machine.core.model.Query;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface MetricStore extends Store {
    Metric save(final Metric metric);
    Metric compute(final Query query);
}
