package net.lunglet.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

public final class Slave {
    private static final class LatchExceptionListener implements ExceptionListener {
        private final CountDownLatch latch;

        public LatchExceptionListener(final CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onException(final JMSException e) {
            e.printStackTrace();
            latch.countDown();
        }
    }

    public static void main(final String[] args) throws JMSException {
        final String brokerURI = System.getProperty("frags.broker.uri", "tcp://localhost:61616");

        final String workQueueName = System.getProperty("frags.workqueue", "work");
        final Queue workQueue = new ActiveMQQueue(workQueueName);

        final String resultsQueueName = System.getProperty("frags.resultqueue", "results");
        final Queue resultsQueue = new ActiveMQQueue(resultsQueueName);

        final int nThreads = Integer.getInteger("frags.nthreads", 2);
        final int consumeFactor = Integer.getInteger("frags.consumefactor", 10);

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>((consumeFactor * nThreads));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, queue);
        executor.prestartAllCoreThreads();
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        while (true) {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURI);
                CountDownLatch latch = new CountDownLatch(1);
                final Connection connection = connectionFactory.createConnection();
                connection.setExceptionListener(new LatchExceptionListener(latch));

                // TODO register some more listeners and whatnot
                // TODO inform the master that it has a new slave

                connection.start();
                new JMSWorker(executor, connection, workQueue, resultsQueue);
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Slave() {
    }
}
