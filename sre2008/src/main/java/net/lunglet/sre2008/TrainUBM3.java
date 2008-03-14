package net.lunglet.sre2008;

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

public final class TrainUBM3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM3.class);

    private static void trainGMM(final H5File dataFile, final GMM gmm) throws InterruptedException, ExecutionException {
        final HDFReader reader = new HDFReader(dataFile);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
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
                // XXX this tended to blow up
                ds.close();
            }
            Future<GMMMAPStats> future = executorService.submit(new Callable<GMMMAPStats>() {
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
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }

    private static void trainGMMwithFlooring(final H5File dataFile, final DiagCovGMM gmm, final FloatVector varFloor)
            throws InterruptedException, ExecutionException {
        final int maxiter = 3;
        for (int iter = 1; iter <= maxiter; iter++) {
            LOGGER.info("Training GMM with " + gmm.getMixtureCount() + " components");
            LOGGER.info("Iteration " + iter + " of " + maxiter);
            if (true) {
                if (iter == 1) {
                    LOGGER.info("Flooring weights to be equal on first iteration");
                    gmm.floorWeights(gmm.getMixtureCount());
                }
            }
            LOGGER.info("Weights before: " + gmm.getWeights());
            trainGMM(dataFile, gmm);
            IOUtils.writeGMM("ubm_orig_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);
            LOGGER.info("Weights after: " + gmm.getWeights());
            LOGGER.info("Flooring variances");
            gmm.floorVariances(varFloor);
            IOUtils.writeGMM("ubm_floored_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);
        }
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        H5File dataFile = new H5File(args[0]);
        DiagCovGMM gmm = GMMUtils.createDiagCovGMM(1, 38);
        FloatVector varianceFloor = null;

        LOGGER.info("Training one component GMM");
        trainGMM(dataFile, gmm);
        LOGGER.info("Setting variance floor to 50% of global variance");
        varianceFloor = gmm.getVariance(0);
        varianceFloor.timesEquals(0.5f);

        for (int i = 2; i <= 32; i++) {
            LOGGER.info("GMM weights before split: " + gmm.getWeights());
            gmm = GMMUtils.splitHeaviest(gmm);
            LOGGER.info("GMM weights after split: " + gmm.getWeights());
            trainGMMwithFlooring(dataFile, gmm, varianceFloor);
        }
        for (int i = 0; i < 6; i++) {
            gmm = GMMUtils.splitAll(gmm);
            trainGMMwithFlooring(dataFile, gmm, varianceFloor);
        }
    }
}
