package net.lunglet.svm.jacksvm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.communication.GridCommunicationSpi;
import org.gridgain.grid.spi.communication.jms.GridJmsCommunicationSpi;
import org.gridgain.grid.spi.discovery.GridDiscoverySpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class SvmTrainGrid {
    private static final int TEST_SPLITS = 2;

    private static final int BACKEND_SPLITS = 3;

    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(true);
        topSpi.setRemoteNodes(true);
        return topSpi;
    }

    private static GridDiscoverySpi createDiscoverySpi() {
        GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
        discoSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>(3);
        // env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
        // INITIAL_CONTEXT_FACTORY);
        // env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        discoSpi.setJndiEnvironment(env);
        discoSpi.setTopicName("topic/gridgain.discovery");
        return discoSpi;
    }

    private static GridCommunicationSpi createCommunicationSpi() {
        GridJmsCommunicationSpi commSpi = new GridJmsCommunicationSpi();
        commSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>(3);
        // env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
        // INITIAL_CONTEXT_FACTORY);
        // env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        commSpi.setJndiEnvironment(env);
        commSpi.setTopicName("topic/gridgain.communication");
        return commSpi;
    }

    private static final List<String> readNames(final String splitName) throws IOException {
        String fileName = "C:/home/albert/LRE2007/keysetc/albert/output/" + splitName + ".txt";
        System.out.println(fileName);
        List<String> names = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            String[] idparts = parts[1].split(",");
            final String name = "/" + idparts[0] + "/" + idparts[1];
            names.add(name);
            line = reader.readLine();
        }
        reader.close();
        return names;
    }

    public static void main(final String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            GridConfigurationAdapter cfg = new GridConfigurationAdapter();
            cfg.setTopologySpi(createTopologySpi());
            cfg.setExecutorService(executorService);
            final Grid grid = GridFactory.start(cfg);
            GridTaskManager<SvmTrainJob> taskManager = new GridTaskManager<SvmTrainJob>(grid, SvmTrainTask.class, 2);
            final List<SvmTrainJob> jobs = new ArrayList<SvmTrainJob>();
            for (int i = 0; i < TEST_SPLITS; i++) {
                for (int j = 0; j < BACKEND_SPLITS; j++) {
                    String modelName = "frontend_" + i + "_" + j;
                    List<String> names = readNames(modelName);
                    jobs.add(new SvmTrainJob(modelName, names));
                }
            }
            taskManager.execute(new GridTaskFactory<SvmTrainJob>() {
                @Override
                public Iterator<SvmTrainJob> iterator() {
                    return jobs.iterator();
                }
            });
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
