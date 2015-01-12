package stream.machine.core.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Stephane on 06/12/2014.
 */
public class Event extends HashMap<String,Object> {
    private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();

    //Identification of the event
    public static String key = "key";
    public static String type = "type";
    public static String name = "name";
    public static String timestamp = "@timestamp";

    public Event() {
    }

    public Event(String name, String type) {
        this();
        setKey(UUID.randomUUID());
        setName(name);
        setType(type);
    }

    public UUID getKey() {
        return (UUID) get(Event.key);
    }

    public void setKey(UUID key) {
        put(Event.key, key);
    }

    public String getName() {
        return (String) get(Event.name);
    }

    public void setName(String name) {
        put(Event.name, name);
    }

    public String getType() {
        return (String) get(Event.type);
    }

    public void setType(String type) {
        put(Event.type, type);
    }

    public DateTime getTimestamp() {
        return (DateTime) get(Event.timestamp);
    }

    public void setTimestamp(DateTime timestamp) {
        put(Event.timestamp, timestamp);
    }
}
