package stream.machine.core.task;

import stream.machine.core.model.Event;

/**
 * Created by Stephane on 15/02/2015.
 */
public class TransformMessage extends TaskMessage {
    private final Event event;


    public TransformMessage(Event event) {
        super(TaskType.Transform);
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}




