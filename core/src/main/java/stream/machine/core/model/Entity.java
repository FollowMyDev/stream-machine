package stream.machine.core.model;

import java.util.UUID;

/**
 * Created by Stephane on 07/12/2014.
 */
public class Entity {
    private final UUID id;

    public Entity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
