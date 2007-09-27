package net.lunglet.svm.jacksvm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.lunglet.gridgain.GridTaskFactory;
import net.lunglet.gridgain.GridTaskManager;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class SvmTrainGrid {
    private static final int TEST_SPLITS = 10;

    private static final int BACKEND_SPLITS = 10;

    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(true);
        topSpi.setRemoteNodes(true);
        return topSpi;
    }

    private static final List<String> readNames(final String splitName) throws IOException {
        String fileName = "C:/home/albert/LRE2007/keysetc/albert/mitpart2/" + splitName + ".txt";
        System.out.println(fileName);
        List<String> names = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            String corpus = parts[0].toLowerCase();
            String filename = parts[2];
            String name = String.format("/%s/%s", corpus, filename);

            // TODO get rid of this hack
            if (!corpus.equals("callfriend") && !filename.equals("tgtd.sph.2.30s.sph")) {
                names.add(name);
            }

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
            GridTaskManager<SvmTrainJob> taskManager = new GridTaskManager<SvmTrainJob>(grid, SvmTrainTask.class, 5);
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
