package net.lunglet.concurrent;

import java.util.concurrent.Callable;

/**
 * A task that is unreliable, i.e., a task that always fails;
 *
 * @author albert
 */
public final class BrokenTask implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        throw new Exception("BrokenTask failed");
    }
}
