package net.lunglet.concurrent;

import java.util.concurrent.TimeUnit;

public interface TaskHandler<V> {
    enum ExpiredAction {
        CANCEL, CANCEL_AND_RETRY, RETRY_CONCURRENTLY
    }

    enum FailedAction {
        ABORT, RETRY
    }

    void cancelledTask(AggregatedFuture<V> aggFuture);

    void completedTask(AggregatedFuture<V> aggFuture);

    ExpiredAction expiredTask(AggregatedFuture<V> aggFuture);

    FailedAction failedTask(AggregatedFuture<V> aggFuture);

    long newTask(AggregatedFuture<V> aggFuture, TimeUnit unit);
}
