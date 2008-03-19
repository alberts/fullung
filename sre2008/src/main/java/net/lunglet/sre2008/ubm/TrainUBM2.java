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
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.math.MatrixMath;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMM;
import net.lunglet.gmm.GMMMAPStats;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.H5Library;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainUBM2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM2.class);

    private static void trainGMM(final H5File dataFile, final GMM gmm) throws InterruptedException, ExecutionException {
        final HDFReader reader = new HDFReader(dataFile);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();
        // TODO put datasets in a list to make sure they stay in the same order
        for (DataSet ds : dataFile.getRootGroup().getDataSets()) {
            final String name;
            final int[] dims;
            // TODO still haven't sorted out HDF5 thread issues?
            // could be because executor service threads and main thread are
            // accessing it at the "same"s time
            synchronized (H5Library.class) {
                name = ds.getName();
                LOGGER.debug("Scanning " + name);
                DataSpace space = ds.getSpace();
                dims = space.getIntDims();
                space.close();
                ds.close();
            }
            Future<GMMMAPStats> future = executorService.submit(new Callable<GMMMAPStats>() {
                @Override
                public GMMMAPStats call() throws Exception {
                    LOGGER.debug("Reading " + name + " " + Arrays.toString(dims));
                    FloatDenseMatrix data = DenseFactory.floatMatrix(dims, Order.ROW, Storage.DIRECT);
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
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        H5File dataFile = new H5File(args[0]);
        DiagCovGMM gmm = GMMUtils.createDiagCovGMM(1, 38);
        FloatVector varianceFloor = null;
        for (int i = 0; i < 11; i++) {
            if (gmm != null) {
                gmm = GMMUtils.splitAll(gmm);
            }
            // 1 iteration for the first time, 2 for the rest
            for (int iter = 0; iter < 3; iter++) {
                int mixtures = (int) Math.pow(2, i);
                LOGGER.info("Training " + mixtures + " mixtures, iter = " + iter);
                trainGMM(dataFile, gmm);
                IOUtils.writeGMM("ubm_before_" + i + "_" + iter + ".h5", gmm);
                if (i > 0) {
                    gmm.floorVariances(varianceFloor);
                } else {
                    varianceFloor = gmm.getVariance(0);
                    // use 50% of global variance when flooring
                    MatrixMath.timesEquals(varianceFloor, 0.5f);
                    break;
                }
                IOUtils.writeGMM("ubm_after_" + i + "_" + iter + ".h5", gmm);
            }
        }
    }
}
