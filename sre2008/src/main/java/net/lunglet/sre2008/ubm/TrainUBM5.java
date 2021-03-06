package net.lunglet.sre2008.ubm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.H5Library;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainUBM5 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM5.class);

    private static List<String> getNames(final H5File h5file) {
        List<String> names = new ArrayList<String>();
        for (Group group : h5file.getRootGroup().getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                names.add(ds.getName());
                ds.close();
            }
            group.close();
        }
        Collections.sort(names);
        return names;
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        H5File dataFile = new H5File(args[0]);
        List<String> names = getNames(dataFile);

        DiagCovGMM gmmVarFloor = GMMUtils.createDiagCovGMM(1, 38);
        LOGGER.info("Training one component GMM");
        trainGMM(dataFile, names, gmmVarFloor);
        IOUtils.writeGMM("ubm_varfloor.h5", gmmVarFloor);

        LOGGER.info("Setting variance floor to 50% of global variance");
        final FloatVector varianceFloor = gmmVarFloor.getVariance(0);
        // XXX maybe tune this flooring constant
        MatrixMath.timesEquals(varianceFloor, 0.5f);

        DiagCovGMM gmm = IOUtils.readDiagCovGMM("origubm.h5");
        for (int i = 0; i < 2; i++) {
            // dump weakest components
            gmm = GMMUtils.keepHeaviest(gmm, 2048 - 384);
            // replace weakest components
            while (gmm.getMixtureCount() < 2048) {
                gmm = GMMUtils.splitHeaviest(gmm);
            }
            // train
            trainGMMwithFlooring(dataFile, names, gmm, varianceFloor, 5);
        }

        dataFile.close();
    }

    private static GMMMAPStats trainGMM(final H5File dataFile, List<String> names, final GMM gmm)
            throws InterruptedException, ExecutionException {
        final HDFReader reader = new HDFReader(dataFile);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();

        for (final String name : names) {
            final int[] dims;
            // TODO still haven't sorted out HDF5 thread issues?
            // could be because executor service threads and main thread are
            // accessing it at the "same" time
            synchronized (H5Library.class) {
                DataSet ds = dataFile.getRootGroup().openDataSet(name);
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
        return globalStats;
    }

    private static void trainGMMwithFlooring(final H5File dataFile, final List<String> names, final DiagCovGMM gmm,
            final FloatVector varFloor, final int maxiter) throws InterruptedException, ExecutionException {
        // TODO use another counter so as to not overwrite files
        int iter = 1;
        LOGGER.info("Flooring weights to be equal on first iteration");
        gmm.floorWeights(gmm.getMixtureCount());
        while (iter <= maxiter) {
            LOGGER.info("Training GMM with " + gmm.getMixtureCount() + " components");
            LOGGER.info("Iteration " + iter + " of " + maxiter);
            LOGGER.info("Weights before: " + gmm.getWeights());
            GMMMAPStats stats = trainGMM(dataFile, names, gmm);
            IOUtils.writeGMM("ubm_weak_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);

            // require at least 10 feature vectors to estimate each parameter
            float nthresh = 10.0f * (2.0f * gmm.getDimension());
            int weakCount = GMMUtils.countWeak(gmm, stats, nthresh);
            LOGGER.info("Replacing {} weak components", weakCount);
            GMMUtils.replaceWeak(gmm, stats, nthresh);

            LOGGER.info("Weights after: " + gmm.getWeights());
            IOUtils.writeGMM("ubm_fixed_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);

            LOGGER.info("Flooring variances");
            gmm.floorVariances(varFloor);
            IOUtils.writeGMM("ubm_floored_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);

            if (weakCount > 0) {
                LOGGER.info("Found some weak components, restarting with equal weights");
                gmm.floorWeights(gmm.getMixtureCount());
                iter = 1;
            } else {
                iter++;
            }
        }
    }
}
