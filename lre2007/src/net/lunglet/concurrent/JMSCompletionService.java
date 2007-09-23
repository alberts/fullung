package net.lunglet.concurrent;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

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
 * A JMS completion service accepts callables, publishes them to a work queue,
 * and consumes their results from a result queue.
 *
 * @author albert
 */
public final class JMSCompletionService<V> implements CompletionService<V> {
    private final class ResultMessageListener implements MessageListener {
        public void onMessage(final Message message) {
            try {
                onMessageImpl(message);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        private void onMessageImpl(final Message message) throws JMSException {
            String correlationID = message.getJMSCorrelationID();
            if (correlationID == null || !results.containsKey(correlationID)) {
                throw new RuntimeException("Received result with invalid correlation ID");
            }
            JMSFuture<V> future = results.remove(correlationID);
            if (future == null) {
                throw new RuntimeException("Unexpected null future");
            }
            synchronized (future) {
                if (future.isCancelled()) {
                    // if future was cancelled, discard the result
                    return;
                }
                if (message.getBooleanProperty(JMSWorker.COMPLETED_PROPERTY)) {
                    future.setResult((V) ((ObjectMessage) message).getObject());
                } else {
                    future.setException((Exception) ((ObjectMessage) message).getObject());
                }
            }
            try {
                completionQueue.put(future);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static final String ID_PROPERTY =
        String.format("%sXXid", JMSCompletionService.class.getName().replaceAll("\\.", "XX"));

    private final MessageProducer callableProducer;

    private final BlockingQueue<Future<V>> completionQueue;

    /** ID that uniquely identifies this completion service instance. */
    private final String id;

    private final ConcurrentHashMap<String, JMSFuture<V>> results;

    private final MessageConsumer resultsConsumer;

    private final Session session;

    public JMSCompletionService(Connection connection, Queue workQueue, Queue resultsQueue) throws JMSException {
        this.id = UUID.randomUUID().toString();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.results = new ConcurrentHashMap<String, JMSFuture<V>>();
        this.completionQueue = new LinkedBlockingQueue<Future<V>>();

        this.callableProducer = session.createProducer(workQueue);
        callableProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        final String messageSelector = String.format("%s='%s'", ID_PROPERTY, id);
        this.resultsConsumer = session.createConsumer(resultsQueue, messageSelector);
        resultsConsumer.setMessageListener(new ResultMessageListener());
    }

    public Future<V> poll() {
        return completionQueue.poll();
    }

    public Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

    public Future<V> submit(final Callable<V> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        final String correlationID = UUID.randomUUID().toString();
        try {
            if (results.containsKey(correlationID)) {
                throw new RuntimeException("Duplicate correlation ID was generated");
            }

            ObjectMessage message = session.createObjectMessage();
            if (callable instanceof JMSTask) {
                ((JMSTask<V>) callable).prepareMessageForTask(message);
            }
            message.setJMSCorrelationID(correlationID);
            message.setStringProperty(ID_PROPERTY, id);
            message.setObject((Serializable) callable);

            // put future into the results map before sending the message
            JMSFuture<V> future = new JMSFuture<V>();
            results.put(correlationID, future);

            callableProducer.send(message);

            return future;
        } catch (JMSException e) {
            results.remove(correlationID);
            throw new RejectedExecutionException(e);
        }
    }

    public Future<V> submit(final Runnable task, final V result) {
        throw new UnsupportedOperationException();
    }

    public Future<V> take() throws InterruptedException {
        return completionQueue.take();
    }
}
