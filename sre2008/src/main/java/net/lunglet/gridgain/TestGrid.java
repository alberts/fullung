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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

// TODO derive job from GridJobAdapter

public final class TestGrid {
    public static abstract class AbstractGrid<R> {
        private final ResultListener<R> resultListener;

        private final Iterable<? extends GridTask<Object, R>> tasks;

        public AbstractGrid(final Iterable<? extends GridTask<Object, R>> tasks, final ResultListener<R> resultListener) {
            this.tasks = tasks;
            this.resultListener = resultListener;
        }

        protected void execute(final Grid grid) {
//            final List<GridTaskFuture<R>> futures = Collections.synchronizedList(new ArrayList<GridTaskFuture<R>>());
            GridTaskListener taskListener = new GridTaskListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onFinished(final GridTaskFuture<?> future) {
                    try {
//                        futures.remove(future);
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
//                futures.add(future);
            }
//            while (futures.size() > 0) {
//                try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    continue;
//                }
//            }
        }
    }

    public static final class LocalGrid<R> extends AbstractGrid<R> {
        public LocalGrid(final Iterable<? extends GridTask<Object, R>> tasks, final ResultListener<R> resultListener) {
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

    public static final class Result implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double[] data = new double[2048 * 38];
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
            return new Result();
        }
    }

    public static class TestTask extends GridTaskAdapter<Object, Result> {
        private static final long serialVersionUID = 1L;

        private final TestJob job;

        private final Random rng = new Random();

        @GridTaskSessionResource
        private GridTaskSession session = null;

        public TestTask(final int i) {
            this.job = new TestJob(i);
        }

        @Override
        public Map<? extends GridJob, GridNode> map(List<GridNode> subgrid, Object arg) throws GridException {
            session.setAttribute("key", new Serializable() {
                private static final long serialVersionUID = 1L;
            });
            Map<TestJob, GridNode> map = new HashMap<TestJob, GridNode>();
            map.put(job, subgrid.get(rng.nextInt(subgrid.size())));
            return map;
        }

        @Override
        public Result reduce(final List<GridJobResult> results) throws GridException {
            return results.get(0).getData();
        }
    }

    public static void main(final String[] args) throws Exception {
        if (false) {
            Logger logger = Logger.getLogger("org.gridgain");
            logger.setLevel(Level.DEBUG);
        }
        List<TestTask> tasks = new ArrayList<TestTask>();
        for (int i = 0; i < 5000; i++) {
            tasks.add(new TestTask(i));
        }
        // TODO test multiple GG threads calling into task listener
        new LocalGrid<Result>(tasks, null).run();
    }
}
