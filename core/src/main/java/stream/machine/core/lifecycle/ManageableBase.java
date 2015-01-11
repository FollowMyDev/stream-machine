package stream.machine.core.lifecycle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ManageableBase implements Manageable {
    protected final Logger logger;
    private final String name;

    protected ManageableBase(String name) {
        if (!StringUtils.isNotBlank(name))
            throw new IllegalArgumentException(
                    "Blank or empty name are not valid");
        logger = LoggerFactory.getLogger(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
