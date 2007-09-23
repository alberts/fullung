package net.lunglet.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Default task handler.
 * <p>
 * Methods are synchronized because the completion service manager and the work
 * queue manager can call into the task handler at the same time.
 *
 * @author albert
 */
public final class DefaultTaskHandler<V> implements TaskHandler<V> {
    @Override
    public synchronized void cancelledTask(AggregatedFuture<V> aggFuture) {
    }

    @Override
    public synchronized void completedTask(AggregatedFuture<V> aggFuture) {
    }

    @Override
    public synchronized ExpiredAction expiredTask(AggregatedFuture<V> aggFuture) {
        return ExpiredAction.CANCEL;
    }

    @Override
    public synchronized FailedAction failedTask(AggregatedFuture<V> aggFuture) {
        return FailedAction.ABORT;
    }

    @Override
    public synchronized long newTask(AggregatedFuture<V> aggFuture, TimeUnit unit) {
        return 0L;
    }
}
