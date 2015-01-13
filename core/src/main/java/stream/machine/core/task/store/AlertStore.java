package stream.machine.core.task.store;

import ro.fortsoft.pf4j.ExtensionPoint;
import stream.machine.core.model.Alert;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface AlertStore extends Store, ExtensionPoint {
    Alert save(final Alert alert);
}
