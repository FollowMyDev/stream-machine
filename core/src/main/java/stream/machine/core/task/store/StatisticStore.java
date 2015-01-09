package stream.machine.core.task.store;

import stream.machine.core.model.Query;
import stream.machine.core.model.Statistic;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface StatisticStore extends Store {
    Statistic aggregate(final Query query);
}
