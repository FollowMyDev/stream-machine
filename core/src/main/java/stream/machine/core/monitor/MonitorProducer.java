package stream.machine.core.monitor;

import stream.machine.core.exception.ApplicationException;

/**
 * Created by Stephane on 28/02/2015.
 */
public interface MonitorProducer {
    void send(Message message) throws ApplicationException;
}
