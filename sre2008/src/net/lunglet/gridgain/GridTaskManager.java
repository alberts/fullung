package net.lunglet.gridgain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.GridTaskFuture;

public final class GridTaskManager<T> {
    private final Grid grid;

    private final int maximumTasks;

    private final Class<? extends GridTask<T>> taskClass;

    public GridTaskManager(final Grid grid, final Class<? extends GridTask<T>> taskClass, final int maximumTasks) {
        this.grid = grid;
        this.taskClass = taskClass;
        this.maximumTasks = maximumTasks;
    }

    public List<Object> execute(final GridTaskFactory<T> taskFactory) throws GridException {
        List<Object> results = new ArrayList<Object>();
        List<GridTaskFuture> futures = new ArrayList<GridTaskFuture>();
        Iterator<T> pendingTasks = taskFactory.iterator();
        while (pendingTasks.hasNext() || futures.size() > 0) {
            List<GridTaskFuture> completedFutures = new ArrayList<GridTaskFuture>();
            for (GridTaskFuture future : futures) {
                if (future.isDone() || future.isCancelled()) {
                    // TODO notify observers of completed task. task factory
                    // could be an observer, in which case it could use the info
                    // to decide which task to make next
                    try {
                        results.add(future.get());
                    } catch (GridException e) {
                        System.err.println("Task failed: " + e.getMessage());
                    }
                    completedFutures.add(future);
                }
            }
            futures.removeAll(completedFutures);
            while (pendingTasks.hasNext() && futures.size() < maximumTasks) {
                T arg = pendingTasks.next();
                System.out.println("executing a new task");
                GridTaskFuture future = grid.execute(taskClass.getName(), arg);
                futures.add(future);
            }
            // TODO make sleep time configurable
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                throw new GridException(null, e);
            }
        }
        return results;
    }
}
