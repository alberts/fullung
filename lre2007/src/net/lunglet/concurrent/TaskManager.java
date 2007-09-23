package net.lunglet.concurrent;

import java.util.IdentityHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.lunglet.concurrent.TaskHandler.ExpiredAction;
import net.lunglet.concurrent.TaskHandler.FailedAction;

public final class TaskManager<V> implements CompletionService<V> {
    /**
     * The completion service manager handles completed tasks.
     */
    private final class CompletionServiceManagerRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Future<V> future = completionService.take();
                    final AggregatedFuture<V> aggFuture;
                    synchronized (TaskManager.this) {
                        aggFuture = futureMap.get(future);
                        if (aggFuture == null) {
                            // futures returned by the completion service that
                            // don't have an associated aggregated future must
                            // have been cancelled
                            if (!future.isCancelled()) {
                                throw new RuntimeException();
                            }
                            continue;
                        }
                        // remove any other futures of this task so that we
                        // don't try to set the result more than once
                        for (Future<V> f : aggFuture.getFutures()) {
                            futureMap.remove(f);
                            f.cancel(true);
                        }
                        // task is done, so get rid of any futures that could
                        // still expire
                        workQueue.removeAll(aggFuture.getDelayedFutures());
                    }
                    try {
                        V results = future.get();
                        aggFuture.setResults(results);
                        taskHandler.completedTask(aggFuture);
                    } catch (CancellationException e) {
                        // this will happen when a task cancelled by the work
                        // queue manager completes
                        aggFuture.setException(e);
                        taskHandler.cancelledTask(aggFuture);
                    } catch (ExecutionException e) {
                        FailedAction action = taskHandler.failedTask(aggFuture);
                        switch (action) {
                        case ABORT:
                            aggFuture.setException(e);
                            taskHandler.completedTask(aggFuture);
                            break;
                        case RETRY:
                            submit(aggFuture);
                            break;
                        default:
                            throw new AssertionError();
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * The work queue manager handles expired tasks.
     */
    private final class WorkQueueManagerRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    DelayedFuture<V> delayedFuture = workQueue.take();
                    Future<V> future = delayedFuture.getFuture();
                    synchronized (TaskManager.this) {
                        final AggregatedFuture<V> aggFuture = futureMap.get(future);
                        if (aggFuture == null) {
                            // task just completed
                            return;
                        }

                        ExpiredAction action = taskHandler.expiredTask(aggFuture);
                        switch (action) {
                        case CANCEL:
                            // The cancelled future will be returned by the
                            // completion service soon after we cancel it,
                            // causing a CancellationException when the
                            // completion service manager gets on the future.
                            future.cancel(true);
                            break;
                        case CANCEL_AND_RETRY:
                            // remove future from map so that completion service
                            // manager doesn't cancel the whole task when this
                            // cancelled future completes
                            futureMap.remove(future);
                            future.cancel(true);
                            submit(aggFuture);
                            break;
                        case RETRY_CONCURRENTLY:
                            submit(aggFuture);
                            break;
                        default:
                            throw new AssertionError();
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private final BlockingQueue<AggregatedFuture<V>> completionQueue;

    private final CompletionService<V> completionService;

    private final Thread completionServiceManager;

    private final IdentityHashMap<Future<V>, AggregatedFuture<V>> futureMap;

    private final TaskHandler<V> taskHandler;

    private final DelayQueue<DelayedFuture<V>> workQueue;

    private final Thread workQueueManager;

    public TaskManager(final CompletionService<V> completionService) {
        this(completionService, new DefaultTaskHandler<V>());
    }

    public TaskManager(final CompletionService<V> completionService, final TaskHandler<V> taskHandler) {
        this.completionService = completionService;
        this.completionQueue = new LinkedBlockingQueue<AggregatedFuture<V>>();
        this.futureMap = new IdentityHashMap<Future<V>, AggregatedFuture<V>>();
        this.workQueue = new DelayQueue<DelayedFuture<V>>();
        this.taskHandler = taskHandler;

        this.workQueueManager = new Thread(new WorkQueueManagerRunnable());
        workQueueManager.start();

        this.completionServiceManager = new Thread(new CompletionServiceManagerRunnable());
        completionServiceManager.start();
    }

    public void awaitTermination() throws InterruptedException {
        workQueueManager.join();
        completionServiceManager.join();
    }

    @Override
    public AggregatedFuture<V> poll() {
        return completionQueue.poll();
    }

    @Override
    public AggregatedFuture<V> poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

    public synchronized void shutdown() throws InterruptedException {
        System.out.println(workQueue.size());
        System.out.println(futureMap.size());
        workQueueManager.interrupt();
        completionServiceManager.interrupt();
    }

    private synchronized void submit(final AggregatedFuture<V> aggFuture) {
        final TimeUnit timeUnit = TimeUnit.NANOSECONDS;
        long timeout = taskHandler.newTask(aggFuture, timeUnit);
        Callable<V> task = aggFuture.getTask();
        Future<V> future = completionService.submit(task);
        DelayedFuture<V> delayedFuture = new DelayedFuture<V>(future, timeout, timeUnit);
        aggFuture.addDelayedFuture(delayedFuture);
        futureMap.put(future, aggFuture);
        if (timeout > 0) {
            // only add task to the work queue if future can timeout
            workQueue.add(delayedFuture);
        }
    }

    @Override
    public synchronized AggregatedFuture<V> submit(final Callable<V> task) {
        AggregatedFuture<V> aggFuture = new AggregatedFuture<V>(task);
        submit(aggFuture);
        return aggFuture;
    }

    @Override
    public Future<V> submit(final Runnable task, final V result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatedFuture<V> take() throws InterruptedException {
        return completionQueue.take();
    }
}
