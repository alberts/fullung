package net.lunglet.gridgain;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.Topic;
import com.sun.messaging.TopicConnectionFactory;
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
import org.gridgain.grid.GridTaskAdapter;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.logger.GridLogger;
import org.gridgain.grid.resources.GridLoggerResource;
import org.gridgain.grid.spi.communication.jms.GridJmsCommunicationSpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class TestGrid {
    public static class TestTask extends GridTaskAdapter<TestJob> {
        private static final long serialVersionUID = 1L;

        private final Random rng = new Random();

        @Override
        public Map<? extends GridJob, GridNode> map(List<GridNode> subgrid, TestJob arg) throws GridException {
            Map<TestJob, GridNode> map = new HashMap<TestJob, GridNode>();
            map.put(arg, subgrid.get(rng.nextInt(subgrid.size())));
            return map;
        }

        @Override
        public Object reduce(List<GridJobResult> results) throws GridException {
            return null;
        }
    }

    public static class TestJob implements GridJob {
        private static final long serialVersionUID = 1L;

        private final int i;
        
        public TestJob(final int i) {
            this.i = i;
        }
        
        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @GridLoggerResource
        private GridLogger logger = null;

        @Override
        public Serializable execute() throws GridException {
            logger.info("Hello " + i);
            return null;
        }
    }

    public static void main(final String[] args) throws Exception {
        System.setProperty("GRIDGAIN_HOME", System.getProperty("user.dir"));
        System.setProperty("gridgain.update.notifier", "false");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        final String gridName = "grid";
        cfg.setGridName(gridName);
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(false);
        topologySpi.setRemoteNodes(true);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setTopologySpi(topologySpi);
        cfg.setExecutorService(executorService);
        TopicConnectionFactory connectionFactory = new TopicConnectionFactory();
        connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostName, "dominatrix.chem.sun.ac.za");
        connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostPort, "7676");
        GridJmsCommunicationSpi commSpi = new GridJmsCommunicationSpi();
        commSpi.setConnectionFactory(connectionFactory);
        commSpi.setTopic(new Topic("gridgaincomm"));
        cfg.setCommunicationSpi(commSpi);
        GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
        discoSpi.setConnectionFactory(connectionFactory);
        discoSpi.setTopic(new Topic("gridgaindisco"));
        cfg.setDiscoverySpi(discoSpi);
        try {
            final Grid grid = GridFactory.start(cfg);
            // without this sleep, things break
            Thread.sleep(10000L);
            List<GridTaskFuture> futures = new ArrayList<GridTaskFuture>();
            for (int i = 0; i < 20000; i++) {
                GridTaskFuture future = grid.execute(TestTask.class.getName(), new TestJob(i));
                futures.add(future);
            }
            for (GridTaskFuture future : futures) {
                future.get();
            }
        } finally {
            GridFactory.stop(gridName, false);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
