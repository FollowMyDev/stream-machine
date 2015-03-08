package stream.machine.core.communication;

import stream.machine.core.exception.ApplicationException;

/**
 * Created by Stephane on 28/02/2015.
 */
public interface MessageProducer {
    <T> void send(String topicName, T data) throws ApplicationException;
}
