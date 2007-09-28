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

    private static final List<Handle2> readData(final List<String> names, final H5File datah5) {
        List<Handle2> handles = new ArrayList<Handle2>();
        for (final String name : names) {
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            int[] indexes = ds.getIntArrayAttribute("indexes");
            String label = ds.getStringAttribute("label");
            ds.close();
            for (int i = 0; i < indexes.length; i++) {
                final int j = i;
                final int index = indexes[i];
                handles.add(new AbstractHandle2(name, index, label) {
                    @Override
                    public FloatVector<?> getData() {
                        DataSet dataset = datah5.getRootGroup().openDataSet(name);
                        DataSpace fileSpace = dataset.getSpace();
                        int len = (int) fileSpace.getDim(1);
                        DataSpace memSpace = new DataSpace(len);
                        FloatDenseVector data = new FloatDenseVector(len, Orientation.COLUMN, Storage.DIRECT);
                        long[] start = {j, 0};
                        long[] count = {1, 1};
                        long[] block = {1, fileSpace.getDim(1)};
                        fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
                        dataset.read(data.data(), FloatType.IEEE_F32LE, memSpace, fileSpace);
                        fileSpace.close();
                        memSpace.close();
                        dataset.close();
                        return data;
                    }
                });
            }
        }
        return handles;
    }

    private static final List<String> readNames(final String splitName) {
        String fileName = "C:/home/albert/LRE2007/keysetc/albert/mitpart2/" + splitName + ".txt";
        System.out.println(fileName);
        List<String> names = new ArrayList<String>();
        try {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return names;
    }

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
                            // TODO can probably remove this when we fix HDF
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
