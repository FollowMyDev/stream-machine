package stream.machine.core.control;

import stream.machine.core.model.Alert;
import stream.machine.core.model.Rule;

/**
 * Created by Stephane on 06/12/2014.
 */
public interface Controller<T> {
    Alert control(T item, Rule rule);
}
