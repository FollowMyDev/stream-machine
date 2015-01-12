package stream.machine.core.task.store;

import stream.machine.core.exception.ApplicationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephane on 05/01/2015.
 */
public class MemoryStoreBase<T> {
    private final Map<String, T> configurations;

    public MemoryStoreBase() {
        this.configurations = new HashMap<String, T>();
    }

    synchronized public List<T> readAll() {
        if (this.configurations.size() == 0) {
            return new ArrayList<T>();
        }
        return new ArrayList<T>(this.configurations.values());
    }

    synchronized public T read(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (this.configurations.containsKey(name)) {
            return this.configurations.get(name);
        }
        return null;
    }

    synchronized public void save(String name, T configuration) throws ApplicationException {
        if (configuration == null || name == null || name.isEmpty()) {
            return;
        }
        if (!this.configurations.containsKey(name)) {
            this.configurations.put(name, configuration);
        }
    }

    synchronized public void update(String name, T configuration) throws ApplicationException {
        if (configuration == null || name == null || name.isEmpty()) {
            return;
        }
        if (this.configurations.containsKey(name)) {
            this.configurations.put(name, configuration);
        }
    }

    synchronized public void delete(String name) throws ApplicationException {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (this.configurations.containsKey(name)) {
            this.configurations.remove(name);
        }
    }
}
