package stream.machine.core.manager;

import stream.machine.core.exception.ApplicationException;

import java.util.UUID;


public interface Manageable {

    UUID getId();

    String getName();

    void start() throws  ApplicationException;

    void stop() throws  ApplicationException;
}
