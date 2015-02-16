package stream.machine.core.manager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class ManageableBase implements Manageable {
    protected final Logger logger;
    private final String name;
    private final UUID id;

    protected ManageableBase(String name) {
        if (!StringUtils.isNotBlank(name))
            throw new IllegalArgumentException(
                    "Blank or empty name are not valid");
        logger = LoggerFactory.getLogger(name);
        this.name = name;
        id = UUID.randomUUID();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
