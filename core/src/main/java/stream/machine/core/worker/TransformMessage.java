package stream.machine.core.worker;

import stream.machine.core.model.Event;

/**
 * Created by Stephane on 15/02/2015.
 */
public class TransformMessage extends WorkerMessage{
    private final Event event;


    public TransformMessage(Event event) {
        super(WorkerType.Transform);
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}




