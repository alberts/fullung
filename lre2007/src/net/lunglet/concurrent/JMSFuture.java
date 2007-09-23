package net.lunglet.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class JMSFuture<V> implements Future<V> {
    private boolean cancelled;

    private boolean done;

    private Exception exception;

    private V results;

    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (isDone()) {
            return false;
        }
        cancelled = true;
        notifyAll();
        return cancelled;
    }

    public synchronized V get() throws InterruptedException, ExecutionException {
        while (!isDone()) {
            wait();
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return results;
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("get(long, TimeUnit) not supported");
    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    public synchronized boolean isDone() {
        return done || isCancelled();
    }

    public synchronized void setException(final Exception exception) {
        if (isDone()) {
            throw new RuntimeException();
        }
        this.exception = exception;
        this.done = true;
        notifyAll();
    }

    public synchronized void setResult(final V results) {
        if (isDone()) {
            throw new RuntimeException();
        }
        this.results = results;
        this.done = true;
        notifyAll();
    }
}
