package stream.machine.worker.Manager;

import io.dropwizard.lifecycle.Managed;
import stream.machine.core.manager.Manageable;

/**
 * Created by Stephane on 15/02/2015.
 */
public class ManagerBase<T extends Manageable> implements Managed {
    private final T manageable;

    public ManagerBase(T manageable) {
        this.manageable = manageable;
    }

    @Override
    public void start() throws Exception {
        if (manageable != null) {
            manageable.start();
        }
    }

    @Override
    public void stop() throws Exception {
        if (manageable != null) {
            manageable.stop();
        }
    }

    public T getManageable(){
        return manageable;
    }
}
