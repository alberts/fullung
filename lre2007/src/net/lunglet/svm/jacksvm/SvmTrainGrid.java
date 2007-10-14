package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.io.HDFWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.lunglet.gridgain.GridTaskFactory;
import net.lunglet.gridgain.GridTaskManager;
import net.lunglet.hdf.H5File;
import net.lunglet.lre.lre07.Constants;
import net.lunglet.lre.lre07.CrossValidationSplits;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.discovery.multicast.GridMulticastDiscoverySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class SvmTrainGrid {
    private static final int NTHREADS = 9;

    private static void compact(final Map<String, CompactJackSVM2Builder> svmBuilders,
            final Map<String, Handle2> trainDataMap) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for (Handle2 handle : trainDataMap.values()) {
            final FloatVector<?> data = handle.getData();
            final int index = handle.getIndex();
            for (final CompactJackSVM2Builder svmBuilder : svmBuilders.values()) {
                tasks.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        svmBuilder.present(data, index);
                        return null;
                    }
                });
            }
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
            tasks.clear();
        }
        executor.shutdown();
        executor.awaitTermination(0L, TimeUnit.MILLISECONDS);
        H5File modelsh5 = new H5File("E:/albert/humodels.h5", H5File.H5F_ACC_TRUNC);
        HDFWriter writer = new HDFWriter(modelsh5);
        for (Map.Entry<String, CompactJackSVM2Builder> entry : svmBuilders.entrySet()) {
            String modelName = entry.getKey();
            System.out.println("building and writing " + modelName);
            JackSVM2 svm = entry.getValue().build();
            writer.write(modelName, svm.getModels());
        }
        writer.close();
    }

    public static void main(final String[] args) throws Exception {
        final String dataFile = "E:/albert/hungrams.h5";
        final CrossValidationSplits cvsplits = Constants.CVSPLITS;
        final List<String> modelNames = new ArrayList<String>();
        for (int i = 0; i < cvsplits.getTestSplits(); i++) {
            for (int j = 0; j < cvsplits.getBackendSplits(); j++) {
                String modelName = "frontend_" + i + "_" + j;
                modelNames.add(modelName);
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(NTHREADS);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(true);
        topologySpi.setRemoteNodes(false);
        cfg.setTopologySpi(topologySpi);
        cfg.setExecutorService(executorService);
        GridMulticastDiscoverySpi discoverySpi = new GridMulticastDiscoverySpi();
        discoverySpi.setHeartbeatFrequency(3000);
        discoverySpi.setLeaveAttempts(5);
        discoverySpi.setMaxMissedHeartbeats(1000);
        discoverySpi.setTimeToLive(8);
        cfg.setDiscoverySpi(discoverySpi);
        H5File datah5 = new H5File(dataFile, H5File.H5F_ACC_RDONLY);
        // get map of handles that discard their data after every read
        final Map<String, Handle2> trainDataMap = cvsplits.getDataMap("frontend", datah5);
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
                            List<Handle2> trainData = cvsplits.getData(modelName, trainDataMap);
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
            List<Object> results = taskManager.execute(taskFactory);
            System.out.println("compacting");
            Map<String, CompactJackSVM2Builder> svmBuilders = new HashMap<String, CompactJackSVM2Builder>();
            for (Object result : results) {
                String modelName = (String) ((Object[]) result)[0];
                JackSVM2 svm = (JackSVM2) ((Object[]) result)[1];
                svmBuilders.put(modelName, svm.getCompactBuilder());
            }
            compact(svmBuilders, trainDataMap);
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
