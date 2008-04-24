package net.lunglet.gridgain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.spi.collision.jobstealing.GridJobStealingCollisionSpi;
import org.gridgain.grid.spi.communication.tcp.GridTcpCommunicationSpi;
import org.gridgain.grid.spi.discovery.multicast.GridMulticastDiscoverySpi;
import org.gridgain.grid.spi.failover.jobstealing.GridJobStealingFailoverSpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class DefaultGrid<R> extends AbstractGrid<R> {
    // XXX use remote jrockit and local = false for 2048 component GMM-SVM
    private static final boolean LOCAL = true;

    public DefaultGrid(final Iterable<? extends GridTask<?, R>> tasks, final ResultListener<R> resultListener) {
        super(tasks, resultListener);
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
        if (LOCAL) {
            collisionSpi.setActiveJobsThreshold(2);
            collisionSpi.setWaitJobsThreshold(4);
            collisionSpi.setStealingEnabled(true);
            collisionSpi.setMaximumStealingAttempts(5);
        } else {
            collisionSpi.setActiveJobsThreshold(0);
            collisionSpi.setWaitJobsThreshold(0);
            collisionSpi.setStealingEnabled(false);
            collisionSpi.setMaximumStealingAttempts(5);
        }
        cfg.setCollisionSpi(collisionSpi);
        GridJobStealingFailoverSpi failoverSpi = new GridJobStealingFailoverSpi();
        failoverSpi.setMaximumFailoverAttempts(5);
        cfg.setFailoverSpi(failoverSpi);
        cfg.setCommunicationSpi(new GridTcpCommunicationSpi());
        cfg.setDiscoverySpi(new GridMulticastDiscoverySpi());
        try {
            final Grid grid = GridFactory.start(cfg);
            execute(grid);
        } finally {
            GridFactory.stop(cfg.getGridName(), false);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
