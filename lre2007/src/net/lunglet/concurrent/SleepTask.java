package net.lunglet.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public final class SleepTask implements Callable<Void> {
    private final int millis;

    public SleepTask(final int timeout, final TimeUnit unit) {
        this.millis = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    @Override
    public Void call() throws Exception {
        Thread.sleep(millis);
        return null;
    }
}
