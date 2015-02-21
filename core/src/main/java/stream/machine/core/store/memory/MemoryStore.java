package stream.machine.core.store.memory;

import com.google.common.collect.ImmutableList;
import stream.machine.core.exception.ApplicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stephane on 05/01/2015.
 */
public class MemoryStore<T> {
    private final Map<String, T> items;

    public MemoryStore() {
        this.items = new ConcurrentHashMap<String, T>();
    }

    public List<T> readAll() {
        if (this.items.size() == 0) {
            return new ArrayList<T>();
        }
        return ImmutableList.copyOf(this.items.values());
    }

    public T read(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (this.items.containsKey(name)) {
            return this.items.get(name);
        }
        return null;
    }

    public void save(String name, T item) throws ApplicationException {
        if (item == null || name == null || name.isEmpty()) {
            return;
        }
        if (!this.items.containsKey(name)) {
            this.items.put(name, item);
        }
    }

     public void update(String name, T item) throws ApplicationException {
        if (item == null || name == null || name.isEmpty()) {
            return;
        }
        if (this.items.containsKey(name)) {
            this.items.put(name, item);
        }
    }

    public void delete(String name) throws ApplicationException {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (this.items.containsKey(name)) {
            this.items.remove(name);
        }
    }
}
