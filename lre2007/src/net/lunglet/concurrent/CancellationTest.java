package net.lunglet.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Ignore;

public final class CancellationTest {
    @Ignore
    public void test() throws InterruptedException {
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<Void>(Executors
                .newSingleThreadExecutor());

        Future<?> f1 = completionService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(0);
                return null;
            }
        });

        Future<?> f2 = completionService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(5000);
                return null;
            }
        });

        Future<?> f3 = completionService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });

        Thread.sleep(1000);

        f3.cancel(true);
        f2.cancel(true);

        try {
            f1.get();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            f2.get();
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            System.out.println("getting f3");
            f3.get();
            System.out.println("got f3");
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
