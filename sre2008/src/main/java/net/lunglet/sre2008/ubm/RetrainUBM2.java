package net.lunglet.sre2008.ubm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMM;
import net.lunglet.gmm.GMMMAPStats;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.H5Library;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RetrainUBM2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrainUBM2.class);

    public static final int NTHREADS = 2;

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NTHREADS);

    private static void trainGMM(final H5File dataFile, final GMM gmm) throws InterruptedException, ExecutionException {
        final HDFReader reader = new HDFReader(dataFile);
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();
        for (DataSet ds : dataFile.getRootGroup().getDataSets()) {
            final String name;
            final int[] dims;
            synchronized (H5Library.class) {
                name = ds.getName();
                LOGGER.debug("Scanning " + name);
                DataSpace space = ds.getSpace();
                dims = space.getIntDims();
                space.close();
                ds.close();
            }
            Future<GMMMAPStats> future = EXECUTOR_SERVICE.submit(new Callable<GMMMAPStats>() {
                @Override
                public GMMMAPStats call() throws Exception {
                    LOGGER.debug("Reading " + name + " " + Arrays.toString(dims));
                    FloatDenseMatrix data = DenseFactory.createFloatMatrix(dims, Order.ROW, Storage.DIRECT);
                    synchronized (H5Library.class) {
                        reader.read(name, data);
                    }
                    // TODO maybe tune fraction as we go along
                    GMMMAPStats stats = new GMMMAPStats(gmm, 0.01);
                    stats.add(data.rowsIterator());
                    return stats;
                }
            });
            futures.add(future);
        }
        GMMMAPStats globalStats = new GMMMAPStats(gmm);
        for (Future<GMMMAPStats> future : futures) {
            GMMMAPStats stats = future.get();
            globalStats.add(stats);
        }
        LOGGER.info("Total log likelihood of data = " + globalStats.getTotalLogLh());
        gmm.doEM(globalStats);
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        DiagCovGMM gmm = IOUtils.readDiagCovGMM(args[0]);
        H5File dataFile = new H5File(args[1]);
        for (int i = 0; i < 5; i++) {
            trainGMM(dataFile, gmm);
            // TODO variance flooring
            IOUtils.writeGMM("ubm_retrain_" + i + ".h5", gmm);
        }
        EXECUTOR_SERVICE.shutdown();
        EXECUTOR_SERVICE.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }
}
