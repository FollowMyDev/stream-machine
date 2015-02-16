package stream.machine.core.store;


import stream.machine.core.model.Statistic;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface StatisticStore extends Store {
    Statistic aggregate();
}
