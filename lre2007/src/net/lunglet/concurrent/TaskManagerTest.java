package net.lunglet.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.lunglet.concurrent.TaskHandler.ExpiredAction;
import net.lunglet.concurrent.TaskHandler.FailedAction;

import org.junit.Ignore;

public final class TaskManagerTest {
    private static class VerboseTaskHandler<V> implements TaskHandler<V> {
        private class TaskInfo {
            private long endTime;

            private long startTime;

            public long getDuration() {
                return endTime - startTime;
            }

            public void setEndTime(final long time) {
                this.endTime = time;
            }

            public void setStartTime(final long time) {
                this.startTime = time;
            }
        }

        private final ExpiredAction expiredAction;

        private final FailedAction failedAction;

        private final Map<AggregatedFuture<V>, TaskInfo> taskInfos;

        /** Timeout in milliseconds. */
        private final long timeout;

        public VerboseTaskHandler() {
            this(0L, ExpiredAction.CANCEL, FailedAction.ABORT);
        }

        public VerboseTaskHandler(FailedAction failedAction) {
            this(0L, ExpiredAction.CANCEL, failedAction);
        }

        public VerboseTaskHandler(long milliTimeout, ExpiredAction expiredAction) {
            this(milliTimeout, expiredAction, FailedAction.ABORT);
        }

        public VerboseTaskHandler(long milliTimeout, ExpiredAction expiredAction, FailedAction failedAction) {
            this.expiredAction = expiredAction;
            this.failedAction = failedAction;
            this.taskInfos = new HashMap<AggregatedFuture<V>, TaskInfo>();
            this.timeout = milliTimeout;
        }

        @Override
        public synchronized void cancelledTask(AggregatedFuture<V> aggFuture) {
            System.out.println("task cancelled");
            TaskInfo taskInfo = taskInfos.get(aggFuture);
            taskInfo.setEndTime(System.currentTimeMillis());
            System.out.println("task took " + taskInfo.getDuration());
        }

        @Override
        public synchronized void completedTask(AggregatedFuture<V> aggFuture) {
            System.out.println("task completed");
            TaskInfo taskInfo = taskInfos.get(aggFuture);
            taskInfo.setEndTime(System.currentTimeMillis());
            System.out.println("task took " + taskInfo.getDuration());
        }

        @Override
        public synchronized ExpiredAction expiredTask(AggregatedFuture<V> aggFuture) {
            System.out.println("task expired");
            return expiredAction;
        }

        @Override
        public synchronized FailedAction failedTask(AggregatedFuture<V> aggFuture) {
            System.out.println("task failed");
            return failedAction;
        }

        @Override
        public synchronized long newTask(AggregatedFuture<V> aggFuture, TimeUnit unit) {
            if (!taskInfos.containsKey(aggFuture)) {
                System.out.println("registering new task");
                taskInfos.put(aggFuture, new TaskInfo());
                TaskInfo taskInfo = taskInfos.get(aggFuture);
                taskInfo.setStartTime(System.currentTimeMillis());
            }
            System.out.println("returning timeout for new task");
            return unit.convert(timeout, TimeUnit.MILLISECONDS);
        }
    }

    @Ignore
    public void testBasics() throws InterruptedException, ExecutionException {
        Executor executor = Executors.newSingleThreadExecutor();
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);
        TaskManager<Void> taskManager = new TaskManager<Void>(completionService, new VerboseTaskHandler<Void>());

        Future<Void> f1 = taskManager.submit(new BrokenTask());
        Future<Void> f2 = taskManager.submit(new SleepTask(0, TimeUnit.MILLISECONDS));
        Future<Void> f3 = taskManager.submit(new SleepTask(0, TimeUnit.MILLISECONDS));

        try {
            f1.get();
            assertTrue(false);
        } catch (ExecutionException expected) {
        }
        f2.get();
        f3.get();

        taskManager.shutdown();
        taskManager.awaitTermination();
    }

    @Ignore
    public void testTooShortTimeout() throws InterruptedException, ExecutionException {
        Executor executor = Executors.newSingleThreadExecutor();
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);
        TaskHandler<Void> taskHandler = new VerboseTaskHandler<Void>(1L, ExpiredAction.RETRY_CONCURRENTLY);
        TaskManager<Void> taskManager = new TaskManager<Void>(completionService, taskHandler);

        TimeUnit unit = TimeUnit.MILLISECONDS;
        Future<?> f1 = taskManager.submit(new SleepTask(20, unit));

        f1.get();

        taskManager.shutdown();
        taskManager.awaitTermination();
    }

    @Ignore
    public void testUnreliableTask() throws InterruptedException, ExecutionException {
        Executor executor = Executors.newSingleThreadExecutor();
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);
        TaskHandler<Void> taskHandler = new VerboseTaskHandler<Void>(FailedAction.RETRY);
        TaskManager<Void> taskManager = new TaskManager<Void>(completionService, taskHandler);

        Future<?> f1 = taskManager.submit(new UnreliableTask(2));
        Future<?> f2 = taskManager.submit(new UnreliableTask(2));
        Future<?> f3 = taskManager.submit(new UnreliableTask(2));
        Future<?> f4 = taskManager.submit(new UnreliableTask(2));

        f1.get();
        f2.get();
        f3.get();
        f4.get();

        taskManager.shutdown();
        taskManager.awaitTermination();
    }
}
