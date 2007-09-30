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
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.H5Library;
import net.lunglet.hdf.SelectionOperator;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseVector;

public final class SvmTrainGrid {
    private static final int TEST_SPLITS = 10;

    private static final int BACKEND_SPLITS = 10;

    public static void main(final String[] args) throws Exception {
        final H5File datah5 = new H5File("G:/czngrams.h5", H5File.H5F_ACC_RDONLY);
        final List<String> modelNames = new ArrayList<String>();
        //final List<SvmTrainJob> jobs = new ArrayList<SvmTrainJob>();
        for (int i = 0; i < TEST_SPLITS; i++) {
            for (int j = 0; j < BACKEND_SPLITS; j++) {
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
        try {
            final Grid grid = GridFactory.start(cfg);
            GridTaskManager<SvmTrainJob> taskManager = new GridTaskManager<SvmTrainJob>(grid, SvmTrainTask.class, 40);
            taskManager.execute(new GridTaskFactory<SvmTrainJob>() {
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
                            List<String> names = readNames(modelName);
                            // XXX probably need to have separate instances of
                            // datah5 for the reduce thread and this main thread
                            List<Handle2> trainData = null;
                            synchronized (H5Library.class) {
                                trainData = readData(names, datah5);
                            }
                            return new SvmTrainJob(modelName, trainData);
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            });
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
        datah5.close();
    }
}
