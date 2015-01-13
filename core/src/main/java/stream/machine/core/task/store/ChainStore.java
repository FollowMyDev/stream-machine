package stream.machine.core.task.store;

import stream.machine.core.model.Chain;
import stream.machine.core.model.Query;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface ChainStore extends Store {
    Chain upsert(final Chain event);

    Chain correlate(final Query query);
}
