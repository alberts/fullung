package net.lunglet.gridgain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.spi.collision.fifoqueue.GridFifoQueueCollisionSpi;
import org.gridgain.grid.spi.communication.tcp.GridTcpCommunicationSpi;
import org.gridgain.grid.spi.discovery.multicast.GridMulticastDiscoverySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class LocalGrid<R> extends AbstractGrid<R> {
    public LocalGrid(final Iterable<? extends GridTask<?, R>> tasks, final ResultListener<R> resultListener) {
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
        topologySpi.setRemoteNodes(false);
        GridFifoQueueCollisionSpi collisionSpi = new GridFifoQueueCollisionSpi();
        collisionSpi.setParallelJobsNumber(2);
        cfg.setCollisionSpi(collisionSpi);
        cfg.setTopologySpi(topologySpi);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setExecutorService(executorService);
        GridTcpCommunicationSpi commSpi = new GridTcpCommunicationSpi();
        cfg.setCommunicationSpi(commSpi);
        GridMulticastDiscoverySpi discoSpi = new GridMulticastDiscoverySpi();
        cfg.setDiscoverySpi(discoSpi);
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
