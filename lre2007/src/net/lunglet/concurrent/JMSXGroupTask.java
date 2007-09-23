package net.lunglet.concurrent;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author albert
 */
public abstract class JMSXGroupTask<V> implements JMSTask<V> {
    private transient boolean firstForConsumer;

    private transient String jmsxGroupID;

    public JMSXGroupTask(final String jmsxGroupID) {
        this.jmsxGroupID = jmsxGroupID;
    }

    public abstract V call() throws Exception;

    protected final boolean isFirstForConsumer() {
        return firstForConsumer;
    }

    public final void prepareMessageForTask(final Message message) throws JMSException {
        message.setStringProperty("JMSXGroupID", jmsxGroupID);
    }

    public final void prepareTaskFromMessage(final Message message) throws JMSException {
        firstForConsumer = message.getBooleanProperty("JMSXGroupFirstForConsumer");
    }
}
