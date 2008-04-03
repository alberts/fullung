package net.lunglet.gridgain;

import java.util.ArrayList;
import java.util.List;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.GridTaskListener;
import org.gridgain.grid.GridTaskTimeoutException;

public abstract class AbstractGrid<J, R> {
    private final Iterable<J> jobs;

    private final ResultListener<R> resultListener;

    private final GridTask<J, R> task;

    public AbstractGrid(final GridTask<J, R> task, final Iterable<J> jobs, final ResultListener<R> resultListener) {
        this.task = task;
        this.jobs = jobs;
        this.resultListener = resultListener;
    }

    protected void execute(final Grid grid) {
        GridTaskListener taskListener = new GridTaskListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onFinished(final GridTaskFuture<?> future) {
                try {
                    if (resultListener == null) {
                        return;
                    }
                    resultListener.onResult((R) future.get());
                } catch (GridTaskTimeoutException e) {
                    e.printStackTrace();
                } catch (GridException e) {
                    e.printStackTrace();
                }
            }
        };
        List<GridTaskFuture<R>> futures = new ArrayList<GridTaskFuture<R>>();
        for (J job : jobs) {
            GridTaskFuture<R> future = grid.execute(task, job, taskListener);
            futures.add(future);
        }
        for (GridTaskFuture<R> future : futures) {
            try {
                future.get();
            } catch (GridTaskTimeoutException e) {
                e.printStackTrace();
            } catch (GridException e) {
                e.printStackTrace();
            }
        }
    }
}
