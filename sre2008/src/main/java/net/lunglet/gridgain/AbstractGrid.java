package net.lunglet.gridgain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.GridTaskListener;
import org.gridgain.grid.GridTaskTimeoutException;

public abstract class AbstractGrid<R> {
    private final Iterable<? extends GridTask<?, R>> tasks;

    private final ResultListener<R> resultListener;

    public AbstractGrid(final Iterable<? extends GridTask<?, R>> tasks, final ResultListener<R> resultListener) {
        this.tasks = tasks;
        this.resultListener = resultListener;
    }

    protected void execute(final Grid grid) {
        final List<GridTaskFuture<R>> futures = Collections.synchronizedList(new ArrayList<GridTaskFuture<R>>());
        GridTaskListener taskListener = new GridTaskListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onFinished(final GridTaskFuture<?> future) {
                try {
                    futures.remove(future);
                    if (resultListener != null) {
                        resultListener.onResult((R) future.get());
                    }
                } catch (GridTaskTimeoutException e) {
                    e.printStackTrace();
                } catch (GridException e) {
                    e.printStackTrace();
                }
            }
        };
        for (GridTask<?, R> task : tasks) {
            final GridTaskFuture<R> future;
            future = grid.execute(task, null, taskListener);
            futures.add(future);
        }
        while (futures.size() > 0) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }
}
