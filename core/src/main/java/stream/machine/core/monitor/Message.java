package stream.machine.core.monitor;

import scala.Serializable;

import java.util.UUID;

/**
 * Created by Stephane on 28/02/2015.
 */
public class Message  implements Serializable {
    private final UUID id;
    private final String subject;

    public Message(String subject) {
        id = UUID.randomUUID();
        this.subject = subject;
    }

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }
}
