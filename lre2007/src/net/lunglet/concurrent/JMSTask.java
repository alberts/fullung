package net.lunglet.concurrent;

import java.io.Serializable;
import java.util.concurrent.Callable;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author albert
 */
public interface JMSTask<V> extends Callable<V>, Serializable {
    /**
     * Called on the producer before the task is serialized.
     */
    void prepareMessageForTask(Message message) throws JMSException;

    /**
     * Called on the consumer (worker) after task has been deserialized.
     */
    void prepareTaskFromMessage(Message message) throws JMSException;
}
