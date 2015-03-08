package stream.machine.core.communication;

/**
 * Created by Stephane on 28/02/2015.
 */
public interface MessageConsumer {
    <T> void onMessage(String topicName, T data);
}
