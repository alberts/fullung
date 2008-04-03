package net.lunglet.gridgain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.GridTaskAdapter;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.GridTaskListener;
import org.gridgain.grid.GridTaskSession;
import org.gridgain.grid.GridTaskTimeoutException;
import org.gridgain.grid.logger.GridLogger;
import org.gridgain.grid.resources.GridLoggerResource;
import org.gridgain.grid.resources.GridTaskSessionResource;
import org.gridgain.grid.spi.collision.fifoqueue.GridFifoQueueCollisionSpi;
import org.gridgain.grid.spi.communication.tcp.GridTcpCommunicationSpi;
import org.gridgain.grid.spi.discovery.multicast.GridMulticastDiscoverySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class TestGrid {
    public static abstract class AbstractGrid<J, R> {
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

    public static final class LocalGrid<J, R> extends AbstractGrid<J, R> {
        public LocalGrid(final GridTask<J, R> task, final Iterable<J> jobs, final ResultListener<R> resultListener) {
            super(task, jobs, resultListener);
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
            collisionSpi.setParallelJobsNumber(1);
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

    public static class TestJob implements GridJob {
        private static final long serialVersionUID = 1L;

        private final int i;

        @GridLoggerResource
        private GridLogger logger = null;

        @GridTaskSessionResource
        private GridTaskSession session = null;

        public TestJob(final int i) {
            this.i = i;
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Serializable execute() throws GridException {
            if (session.getAttribute("key") == null) {
                logger.error("BORK " + i + " " + session.getId());
            }
            return null;
        }
    }

    public static class TestTask extends GridTaskAdapter<TestJob, Object> {
        private static final long serialVersionUID = 1L;

        private final Random rng = new Random();

        @GridTaskSessionResource
        private GridTaskSession session = null;

        @Override
        public Map<? extends GridJob, GridNode> map(List<GridNode> subgrid, TestJob arg) throws GridException {
            session.setAttribute("key", new Serializable() {
                private static final long serialVersionUID = 1L;
            });
            Map<TestJob, GridNode> map = new HashMap<TestJob, GridNode>();
            map.put(arg, subgrid.get(rng.nextInt(subgrid.size())));
            return map;
        }

        @Override
        public Object reduce(List<GridJobResult> results) throws GridException {
            return null;
        }
    }

    public static void main(final String[] args) throws Exception {
        List<TestJob> tasks = new ArrayList<TestJob>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(new TestJob(i));
        }
        new LocalGrid<TestJob, Object>(new TestTask(), tasks, null).run();
    }
}
