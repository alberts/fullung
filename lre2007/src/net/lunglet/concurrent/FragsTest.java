package net.lunglet.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Ignore;

public final class FragsTest {
    private void doWork(final CompletionService<String> completionService) throws InterruptedException,
            ExecutionException {
        completionService.submit(new UppercaseStringTask("123", "hello1"));
        Future<String> hello2future = completionService.submit(new UppercaseStringTask("123", "hello2"));
        completionService.submit(new UppercaseStringTask("456", "hello3"));
        completionService.submit(new UppercaseStringTask("456", "hello4"));
        completionService.submit(new UppercaseStringTask("123", "hello5"));
        Future<String> diefuture = completionService.submit(new UppercaseStringTask("123", "die"));
        completionService.submit(new UppercaseStringTask("456", "die"));
        assertEquals("HELLO2", hello2future.get());
        try {
            diefuture.get();
            assertTrue(false);
        } catch (ExecutionException expected) {
        }
        int successfulResults = 0;
        for (int i = 0; i < 6; i++) {
            Future<String> result = completionService.poll();
            assertNotNull(result);
            try {
                String resultStr = result.get();
                assertTrue(resultStr.startsWith("HELLO"));
                successfulResults++;
            } catch (ExecutionException ignored) {
            }
        }
    }

//    @Test(timeout = 10000)
    @Ignore
    public void test() throws JMSException, InterruptedException, ExecutionException {
        // TODO get logging working properly

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        final Connection connection = connectionFactory.createConnection();
        Queue workQueue = new ActiveMQQueue("work-1234");
        Queue resultQueue = new ActiveMQQueue("results-1234");

        // XXX the worker will typically be set up on another machine with its
        // own connection
        final int nThreads = 4;
        // Use a queue with a finite capacity combined with a CallerRunsPolicy
        // rejected execution handler to prevent the first worker to connect
        // from consuming all available work in the JMS queue by slowing down
        // the consumer. However, setting extraConsumeFactor too low can cause
        // the thread pool to starve while waiting for the message listener
        // (which is now busy doing work itself) to enqueue more work from the
        // JMS work queue to the local queue.
        float extraConsumeFactor = 0.5f;
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>((int) (extraConsumeFactor * nThreads));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, queue);
        executor.prestartAllCoreThreads();
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        JMSWorker worker = new JMSWorker(executor, connection, workQueue, resultQueue);

        connection.start();

        JMSCompletionService<String> completionService =
            new JMSCompletionService<String>(connection, workQueue, resultQueue);

        doWork(completionService);

        worker.close();
        executor.awaitTermination(0L, TimeUnit.MILLISECONDS);
        connection.close();
    }
}
