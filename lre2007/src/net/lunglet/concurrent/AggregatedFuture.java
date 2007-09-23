package net.lunglet.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class AggregatedFuture<V> implements Future<V> {
    private boolean cancelled;

    private final List<DelayedFuture<V>> delayedFutures;

    private boolean done;

    private Exception exception;

    private V results;

    private final Callable<V> task;

    public AggregatedFuture(final Callable<V> task) {
        this.task = task;
        this.delayedFutures = new ArrayList<DelayedFuture<V>>();
        this.cancelled = false;
        this.done = false;
    }

    public void addDelayedFuture(DelayedFuture<V> future) {
        delayedFutures.add(future);
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (isDone()) {
            return false;
        }
        cancelled = true;
        notifyAll();
        return cancelled;
    }

    @Override
    public synchronized V get() throws InterruptedException, ExecutionException {
        while (!isDone()) {
            wait();
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return results;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("get(long, TimeUnit) not supported");
    }

    public List<DelayedFuture<V>> getDelayedFutures() {
        return delayedFutures;
    }

    public List<Future<V>> getFutures() {
        List<Future<V>> futures = new ArrayList<Future<V>>();
        for (DelayedFuture<V> delayedFuture : delayedFutures) {
            futures.add(delayedFuture.getFuture());
        }
        return futures;
    }

    public Callable<V> getTask() {
        return task;
    }

    @Override
    public synchronized boolean isCancelled() {
        return cancelled;
    }

    @Override
    public synchronized boolean isDone() {
        return done || isCancelled();
    }

    public boolean removeDelayedFuture(final DelayedFuture<V> future) {
        return delayedFutures.remove(future);
    }

    public synchronized void setException(final Exception exception) {
        if (isDone()) {
            throw new RuntimeException();
        }
        this.exception = exception;
        this.done = true;
        notifyAll();
    }

    public synchronized void setResults(final V results) {
        if (isDone()) {
            throw new RuntimeException();
        }
        this.results = results;
        this.done = true;
        notifyAll();
    }
}
