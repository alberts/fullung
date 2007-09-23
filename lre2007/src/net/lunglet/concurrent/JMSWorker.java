package net.lunglet.concurrent;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * A JMS worker consumes Callables from a work queue, uses an executor to call
 * them, and publishes the results to a result queue.
 *
 * @author albert
 */
public final class JMSWorker {
    private final class JMSCallableAdapter implements Callable<Void> {
        private final Callable<?> callable;

        private final String completionServiceID;

        private final String correlationID;

        public JMSCallableAdapter(Callable<?> callable, String completionServiceID, String correlationID) {
            this.callable = callable;
            this.completionServiceID = completionServiceID;
            this.correlationID = correlationID;
        }

        public Void call() throws Exception {
            Object result;
            boolean completed = false;
            try {
                result = callable.call();
                completed = true;
            } catch (Exception e) {
                result = e;
            }

            // TODO could possibly only lock session while doing send
            synchronized (JMSWorker.this) {
                ObjectMessage resultMessage = session.createObjectMessage();
                resultMessage.setObject((Serializable) result);
                resultMessage.setBooleanProperty(COMPLETED_PROPERTY, completed);
                resultMessage.setStringProperty(JMSCompletionService.ID_PROPERTY, completionServiceID);
                resultMessage.setJMSCorrelationID(correlationID);
                resultsProducer.send(resultMessage);
            }
            return null;
        }
    }

    private final class TaskMessageListener implements MessageListener {
        public void onMessage(final Message message) {
            try {
                onMessageImpl(message);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        private void onMessageImpl(final Message message) throws JMSException {
            JMSTask<?> task = (JMSTask<?>) ((ObjectMessage) message).getObject();
            task.prepareTaskFromMessage(message);
            String completionServiceID = message.getStringProperty(JMSCompletionService.ID_PROPERTY);
            String correlationID = message.getJMSCorrelationID();
            // delegate task to the real executor
            executor.submit(new JMSCallableAdapter(task, completionServiceID, correlationID));
        }
    }

    public static final String COMPLETED_PROPERTY = String.format("%sXXcompleted",
            JMSWorker.class.getName().replaceAll("\\.", "XX"));

    private final MessageConsumer callableConsumer;

    private final ExecutorService executor;

    private final MessageProducer resultsProducer;

    private final Session session;

    public JMSWorker(final ExecutorService executor, final Connection connection, final Queue workQueue,
            final Queue resultsQueue) throws JMSException {
        this.executor = executor;
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // TODO might want to tune prefetch size on this consumer to prevent it
        // from grabbing too much work
        this.callableConsumer = session.createConsumer(workQueue);
        callableConsumer.setMessageListener(new TaskMessageListener());
        this.resultsProducer = session.createProducer(resultsQueue);
        resultsProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public synchronized void close() throws JMSException {
        session.close();
    }
}
