package stream.machine.core.store;

import stream.machine.core.model.Chain;


/**
 * Created by Stephane on 07/12/2014.
 */
public interface ChainStore extends Store {
    Chain upsert(final Chain event);

    Chain correlate();
}
