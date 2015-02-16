package stream.machine.core.store;

import stream.machine.core.model.Metric;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface MetricStore extends Store {
    Metric save(final Metric metric);

    Metric compute();
}
