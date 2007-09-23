package net.lunglet.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DelayedFuture<V> implements Delayed {
    /** Base of nanosecond timings, to avoid wrapping */
    private static final long NANO_ORIGIN = System.nanoTime();

    private static final AtomicLong SEQUENCER = new AtomicLong(0);

    /**
     * Returns nanosecond time offset by origin
     */
    private static long now() {
        return System.nanoTime() - NANO_ORIGIN;
    }

    private final Future<V> future;

    private final long sequenceNumber;

    private final long time;

    public DelayedFuture(final Future<V> future, final long timeout, TimeUnit unit) {
        this.future = future;
        this.time = TimeUnit.NANOSECONDS.convert(timeout, unit) + now();
        this.sequenceNumber = SEQUENCER.getAndIncrement();
    }

    @Override
    public int compareTo(final Delayed other) {
        // compare zero ONLY if same object
        if (other == this) {
            return 0;
        }
        if (other instanceof DelayedFuture) {
            DelayedFuture<?> x = (DelayedFuture<?>) other;
            long diff = time - x.time;
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else if (sequenceNumber < x.sequenceNumber) {
                return -1;
            } else {
                return 1;
            }
        }
        long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }

    @Override
    public long getDelay(final TimeUnit unit) {
        long d = unit.convert(time - now(), TimeUnit.NANOSECONDS);
        return d;
    }

    public Future<V> getFuture() {
        return future;
    }
}
