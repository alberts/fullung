package net.lunglet.sre2008.ubm;

import java.io.File;
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
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO use SoftReferences to keep data in memory

public final class TrainUBM6 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM6.class);

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
        LOGGER.info("Reading names of datasets in data file");
        H5File dataFile = new H5File(args[0]);
        List<String> names = getNames(dataFile);

        DiagCovGMM gmmVarFloor = GMMUtils.createDiagCovGMM(1, 79);
        LOGGER.info("Training one component GMM");
        trainGMM(dataFile, names, gmmVarFloor);
        IOUtils.writeGMM("ubm_varfloor.h5", gmmVarFloor);

        LOGGER.info("Setting variance floor to 50% of global variance");
        final FloatVector varianceFloor = gmmVarFloor.getVariance(0);
        // XXX maybe tune this flooring constant
        MatrixMath.timesEquals(varianceFloor, 0.5f);
        LOGGER.info("Variance floor: {}", varianceFloor);

        DiagCovGMM gmm = null;
        if (new File("origubm.h5").exists()) {
            gmm = IOUtils.readDiagCovGMM("origubm.h5");
            // train here first so that we don't immediately split the original
            trainGMMwithFlooring(dataFile, names, gmm, varianceFloor, 3);
        } else {
            gmm = gmmVarFloor;
        }
        while (gmm.getMixtureCount() < 512) {
            gmm = GMMUtils.splitAll(gmm);
            trainGMMwithFlooring(dataFile, names, gmm, varianceFloor, 3);
        }
        for (int i = 0; i < 5; i++) {
            gmm = GMMUtils.keepHeaviest(gmm, 512 - 128);
            while (gmm.getMixtureCount() < 512) {
                gmm = GMMUtils.splitHeaviest(gmm);
            }
            trainGMMwithFlooring(dataFile, names, gmm, varianceFloor, 3);
        }
        trainGMMwithFlooring(dataFile, names, gmm, varianceFloor, 5);

        dataFile.close();
    }

    private static GMMMAPStats trainGMM(final H5File dataFile, List<String> names, final GMM gmm)
            throws InterruptedException, ExecutionException {
        // use a large buffer here and heap matrices for the data
        final HDFReader reader = new HDFReader(dataFile, 8388608);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();
        for (final String name : names) {
            final int[] dims;
            DataSet ds = dataFile.getRootGroup().openDataSet(name);
            LOGGER.debug("Scanning " + name);
            DataSpace space = ds.getSpace();
            dims = space.getIntDims();
            space.close();
            // XXX this tended to blow up
            ds.close();

            Future<GMMMAPStats> future = executorService.submit(new Callable<GMMMAPStats>() {
                @Override
                public GMMMAPStats call() throws Exception {
                    LOGGER.debug("Reading " + name + " " + Arrays.toString(dims));
                    // use heap storage here to avoid problems with allocation
                    // of many large direct buffers
                    FloatDenseMatrix data = DenseFactory.floatMatrix(dims, Order.ROW, Storage.HEAP);
                    reader.read(name, data);
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
        // TODO keep global counter over all GMMs so that everything is saved
        int count = 1;
        int iter = 1;
        LOGGER.info("Flooring weights to be equal on first iteration");
        gmm.floorWeights(gmm.getMixtureCount());
        while (iter <= maxiter) {
            LOGGER.info("Training GMM with " + gmm.getMixtureCount() + " components");
            LOGGER.info("Iteration " + iter + " of " + maxiter);
            LOGGER.info("Weights before: " + gmm.getWeights());
            GMMMAPStats stats = trainGMM(dataFile, names, gmm);
            IOUtils.writeGMM("ubm_weak_" + gmm.getMixtureCount() + "_" + count + ".h5", gmm);

            // require at least 50 feature vectors to estimate each parameter
            float nthresh = 50.0f * (2.0f * gmm.getDimension());
            int weakCount = GMMUtils.countWeak(gmm, stats, nthresh);
            LOGGER.info("Replacing {} weak components", weakCount);
            GMMUtils.replaceWeak(gmm, stats, nthresh);

            LOGGER.info("Weights after: " + gmm.getWeights());
            IOUtils.writeGMM("ubm_fixed_" + gmm.getMixtureCount() + "_" + count + ".h5", gmm);

            LOGGER.info("Flooring variances");
            gmm.floorVariances(varFloor);
            IOUtils.writeGMM("ubm_floored_" + gmm.getMixtureCount() + "_" + count + ".h5", gmm);

            if (weakCount > 0) {
                LOGGER.info("Found some weak components, restarting with equal weights");
                gmm.floorWeights(gmm.getMixtureCount());
                iter = 1;
            } else {
                iter++;
            }
            count++;
        }
        IOUtils.writeGMM("ubm_final_" + gmm.getMixtureCount() + ".h5", gmm);
    }
}
