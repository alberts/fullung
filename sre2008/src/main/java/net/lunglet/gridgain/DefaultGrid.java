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
import org.gridgain.grid.spi.collision.jobstealing.GridJobStealingCollisionSpi;
import org.gridgain.grid.spi.communication.tcp.GridTcpCommunicationSpi;
import org.gridgain.grid.spi.discovery.multicast.GridMulticastDiscoverySpi;
import org.gridgain.grid.spi.failover.jobstealing.GridJobStealingFailoverSpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class DefaultGrid<T, R> {
    private final Class<? extends GridTask<T, R>> taskClass;

    private final Iterable<T> tasks;

    private final ResultHandler<R> resultHandler;

    public DefaultGrid(final Class<? extends GridTask<T, R>> taskClass, final Iterable<T> tasks,
            final ResultHandler<R> resultHandler) {
        this.taskClass = taskClass;
        this.tasks = tasks;
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
        topologySpi.setRemoteNodes(true);
        cfg.setTopologySpi(topologySpi);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setExecutorService(executorService);
        GridJobStealingCollisionSpi collisionSpi = new GridJobStealingCollisionSpi();
        collisionSpi.setActiveJobsThreshold(0);
        collisionSpi.setWaitJobsThreshold(0);
        collisionSpi.setStealingEnabled(false);
        collisionSpi.setMaximumStealingAttempts(5);
        cfg.setCollisionSpi(collisionSpi);
        GridJobStealingFailoverSpi failoverSpi = new GridJobStealingFailoverSpi();
        failoverSpi.setMaximumFailoverAttempts(5);
        cfg.setFailoverSpi(failoverSpi);
        cfg.setCommunicationSpi(new GridTcpCommunicationSpi());
        cfg.setDiscoverySpi(new GridMulticastDiscoverySpi());
        try {
            final Grid grid = GridFactory.start(cfg);
            List<GridTaskFuture<R>> futures = new ArrayList<GridTaskFuture<R>>();
            for (T task : tasks) {
                GridTaskFuture<R> future = grid.execute(taskClass, task);
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
