package net.lunglet.concurrent;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * A task that is unreliable, i.e., a task that fails randomly.
 *
 * @author albert
 */
public final class UnreliableTask implements Callable<Void> {
    private final int failureFactor;

    public UnreliableTask(final int failureFactor) {
        this.failureFactor = failureFactor;
    }

    @Override
    public Void call() throws Exception {
        Random random = new Random();
        if (0 == random.nextInt(failureFactor)) {
            throw new Exception("UnreliableTask failed");
        }
        return null;
    }
}
