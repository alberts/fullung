package net.lunglet.svm.jacksvm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.lunglet.gridgain.GridTaskFactory;
import net.lunglet.gridgain.GridTaskManager;
import net.lunglet.hdf.H5File;
import net.lunglet.lre.lre07.CrossValidationSplits;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.discovery.multicast.GridMulticastDiscoverySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class SvmTrainGrid {
    public static void main(final String[] args) throws Exception {
        // create empty H5 file for SVM models
        new H5File("G:/czmodels.h5").close();
        final String dataFile = "G:/czngrams.h5";
        final CrossValidationSplits cvsplits = new CrossValidationSplits(10, 10);
        final List<String> modelNames = new ArrayList<String>();
        for (int i = 0; i < cvsplits.getTestSplits(); i++) {
            for (int j = 0; j < cvsplits.getBackendSplits(); j++) {
                String modelName = "frontend_" + i + "_" + j;
                modelNames.add(modelName);
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(false);
        topologySpi.setRemoteNodes(true);
        cfg.setTopologySpi(topologySpi);
        cfg.setExecutorService(executorService);
        GridMulticastDiscoverySpi discoverySpi = new GridMulticastDiscoverySpi();
        discoverySpi.setHeartbeatFrequency(3000);
        discoverySpi.setLeaveAttempts(5);
        discoverySpi.setMaxMissedHeartbeats(1000);
        discoverySpi.setTimeToLive(8);
        cfg.setDiscoverySpi(discoverySpi);
        H5File datah5 = new H5File(dataFile, H5File.H5F_ACC_RDONLY);
        // XXX this datah5 instance is also used when models get compacted,
        // which caused problems when tasks were still being manufactured in a
        // way that required datah5 to be read
        final Map<String, Handle2> frontendHandles = cvsplits.getDataMap("frontend", datah5);
        try {
            final Grid grid = GridFactory.start(cfg);
            GridTaskFactory<SvmTrainJob> taskFactory = new GridTaskFactory<SvmTrainJob>() {
                @Override
                public Iterator<SvmTrainJob> iterator() {
                    final Iterator<String> it = modelNames.iterator();
                    return new Iterator<SvmTrainJob>() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public SvmTrainJob next() {
                            String modelName = it.next();
                            if (modelName == null) {
                                return null;
                            }
                            List<Handle2> trainData = cvsplits.getData(modelName, frontendHandles);
                            return new SvmTrainJob(modelName, trainData);
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
            GridTaskManager<SvmTrainJob> taskManager = null;
            taskManager = new GridTaskManager<SvmTrainJob>(grid, SvmTrainTask.class, 100);
            taskManager.execute(taskFactory);
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
