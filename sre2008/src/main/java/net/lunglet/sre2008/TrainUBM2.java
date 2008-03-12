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
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;

public final class TrainUBM2 {
    private static void writeGMM(final H5File gmmFile, final GMM gmm) {
        Group root = gmmFile.getRootGroup();
        Group means = root.createGroup("/means");
        means.close();
        Group variances = root.createGroup("/variances");
        variances.close();
        HDFWriter writer = new HDFWriter(gmmFile);
        writer.write("/weights", DenseFactory.directCopy(gmm.getWeights()));
        for (int i = 0; i < gmm.getMixtureCount(); i++) {
            writer.write("/means/" + i, DenseFactory.directCopy(gmm.getMean(i)));
            writer.write("/variances/" + i, DenseFactory.directCopy(gmm.getVariance(i)));
        }
    }

    private static void trainGMM(final H5File dataFile, final GMM gmm) throws InterruptedException, ExecutionException {
        final HDFReader reader = new HDFReader(dataFile);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();
        // TODO put datasets in a list to make sure they stay in the same order
        for (DataSet ds : dataFile.getRootGroup().getDataSets()) {
            final String name = ds.getName();
            System.out.println("Scanning " + name);
            DataSpace space = ds.getSpace();
            final int[] dims = space.getIntDims();
            space.close();
            ds.close();
            Future<GMMMAPStats> future = executorService.submit(new Callable<GMMMAPStats>() {
                @Override
                public GMMMAPStats call() throws Exception {
                    System.out.println("Reading " + name + " " + Arrays.toString(dims));
                    FloatDenseMatrix data = DenseFactory.createFloatMatrix(dims, Order.ROW, Storage.DIRECT);
                    synchronized (reader) {
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
        System.out.println("total ll = " + globalStats.getTotalLogLh());
        gmm.doEM(globalStats);
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        // TODO maybe leave weights equal until near the end
        // TODO use as much data as possible
        // TODO use data from many channels
        // TODO split only mixture with largest weight until we get to 32 or so
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
                System.out.println("-------> Training " + mixtures + " mixtures, iter = " + iter);
                trainGMM(dataFile, gmm);
                H5File ubmFile = new H5File("ubm_before_" + i + "_" + iter + ".h5", H5File.H5F_ACC_TRUNC);
                writeGMM(ubmFile, gmm);
                ubmFile.close();
                if (i > 0) {
                    gmm.floorVariances(varianceFloor);
                } else {
                    varianceFloor = gmm.getVariance(0);
                    // use 50% of global variance when flooring
                    varianceFloor.timesEquals(0.5f);
                    break;
                }
                ubmFile = new H5File("ubm_after_" + i + "_" + iter + ".h5", H5File.H5F_ACC_TRUNC);
                writeGMM(ubmFile, gmm);
                ubmFile.close();
            }
        }
    }
}
