package net.lunglet.gridgain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.spi.collision.fifoqueue.GridFifoQueueCollisionSpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class LocalGrid<T, R> {
    private final Class<? extends GridTask<T, R>> taskClass;

    private final Iterable<T> jobs;

    private final ResultHandler<R> resultHandler;

    public LocalGrid(final Class<? extends GridTask<T, R>> taskClass, final Iterable<T> jobs,
            final ResultHandler<R> resultHandler) {
        this.taskClass = taskClass;
        this.jobs = jobs;
        this.resultHandler = resultHandler;
    }

    public void run() throws Exception {
        System.setProperty("GRIDGAIN_HOME", System.getProperty("user.dir"));
        System.setProperty("gridgain.update.notifier", "false");
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        cfg.setGridName("grid");
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(true);
        topologySpi.setRemoteNodes(false);
        GridFifoQueueCollisionSpi collisionSpi = new GridFifoQueueCollisionSpi();
        collisionSpi.setParallelJobsNumber(2);
        cfg.setCollisionSpi(collisionSpi);
        cfg.setTopologySpi(topologySpi);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setExecutorService(executorService);
        try {
            final Grid grid = GridFactory.start(cfg);
            List<GridTaskFuture<R>> futures = new ArrayList<GridTaskFuture<R>>();
            for (T job : jobs) {
                GridTaskFuture<R> future = grid.execute(taskClass, job);
                futures.add(future);
            }
            List<GridTaskFuture<R>> completedFutures = new ArrayList<GridTaskFuture<R>>();
            while (futures.size() > 0) {
                for (GridTaskFuture<R> future : futures) {
                    if (future.isDone() || future.isCancelled()) {
                        R result = future.get();
                        if (resultHandler != null) {
                            resultHandler.onResult(result);
                        }
                        completedFutures.add(future);
                    }
                }
                futures.removeAll(completedFutures);
                completedFutures.clear();
                Thread.sleep(1000L);
            }
        } finally {
            GridFactory.stop(cfg.getGridName(), false);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
