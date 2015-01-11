package stream.machine.core.lifecycle;

import stream.machine.core.exception.ApplicationException;


public interface Manageable {
    String getName();

    void start() throws  ApplicationException;

    void stop() throws  ApplicationException;
}
