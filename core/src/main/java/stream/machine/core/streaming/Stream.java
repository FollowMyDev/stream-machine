package stream.machine.core.streaming;

import java.util.UUID;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface Stream<T> {
    UUID getStreamId();
}
