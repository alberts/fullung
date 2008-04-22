package net.lunglet.sre2008;

import com.dvsoft.sv.toolbox.gmm.EigenMapTrain;
import com.dvsoft.sv.toolbox.gmm.FrameLvlBgr;
import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.gmm.SuperVector;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.util.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainGMM3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainGMM3.class);

    public static final int MAP_ITERATIONS = 5;

    public static void main(final String[] args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        if (args.length != 2 && args.length != 3) {
            LOGGER.error("Usage: TrainGMM3 MFCCLIST UBM [CHANNEL]");
            System.exit(1);
        }
        String mfccListFilename = args[0];
        String ubmFilename = args[1];
        String channelFilename = args.length == 3 ? args[2] : null;

        List<String> names = readFilelist(mfccListFilename);
        JMapGMM ubm = readUBM(ubmFilename);
        JMatrix channel = readChannel(channelFilename);

//        final int threads = 8;
//        final int queueCapacity = 15 * threads;
        final int threads = 4;
        final int queueCapacity = 10 * threads;
        ExecutorService executorService = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            train(names, ubm, channel, executorService);
        } finally {
            LOGGER.info("Shutting down");
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }

    public static JMatrix readChannel(String umatFile) {
        LOGGER.info("Reading channel factors from {}", umatFile);
        HDFReader reader = new HDFReader(umatFile);
        int dim = 512 * 38;
        int k = 40;
        FloatDenseMatrix channelSpace = DenseFactory.floatRowDirect(new int[]{dim, k});
        reader.read("/U", channelSpace);
        reader.close();
        return new JMatrix(channelSpace.toRowArrays());
    }

    public static final List<String> readFilelist(final String name) throws IOException {
        /*
         * TODO make this extensible, e.g., make it possible to check that
         * everything is an HDF file that has a group called /gmm, with the same
         * dimensions
         */
        LOGGER.info("Reading file list from {}", name);
        List<String> names = new ArrayList<String>();
        BufferedReader lineReader = new BufferedReader(new FileReader(name));
        try {
            String line = lineReader.readLine();
            while (line != null) {
                line = line.trim();
                if (!new File(line).isFile()) {
                    throw new FileNotFoundException(line + " is not a file");
                }
                names.add(line);
                line = lineReader.readLine();
            }
            Collections.sort(names);
            return names;
        } finally {
            lineReader.close();
        }
    }

    public static JMapGMM readUBM(final String ubmFilename) {
        LOGGER.info("Reading UBM from {}", ubmFilename);
        DiagCovGMM ubm = IOUtils.readDiagCovGMM(ubmFilename);
        TrainGMM.checkGMM(ubm);
        return Converters.convert(ubm);
    }

    private static void train(final List<String> names, final JMapGMM ubm, final JMatrix channel,
            final ExecutorService executorService) {
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        final HDFReader reader = new HDFReader(16 * 1024 * 1024);
        for (final String name : names) {
            H5File h5file = new H5File(name);
            Group mfccGroup = h5file.getRootGroup().openGroup("/mfcc");
            int i = 0;
            for (DataSet dataset : mfccGroup.getDataSets()) {
                final int j = i;
                final String hdfName = dataset.getName();
                final int[] dims = dataset.getIntDims();
                dataset.close();
                Future<Void> future = executorService.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        LOGGER.info("Reading {} from {}", hdfName, name);
                        H5File h5file2 = new H5File(name);
                        FloatDenseMatrix data = DenseFactory.floatRowHeap(dims[0], dims[1]);
                        synchronized (reader) {
                            reader.read(h5file2, hdfName, data);
                        }
                        h5file2.close();
                        float[] gmmsv = trainGMM(data, ubm, channel);
                        String outputName = name + "." + j + ".gmm";
                        LOGGER.info("Writing GMM to {}", outputName);
                        HDFWriter writer = new HDFWriter(outputName);
                        writer.write("/gmm", DenseFactory.floatVector(gmmsv));
                        writer.close();
                        return null;
                    }
                });
                futures.add(future);
                i++;
            }
            h5file.close();
        }
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        futures.clear();
        reader.close();
    }

    public static float[] trainGMM(final FloatDenseMatrix dataMat, final JMapGMM ubm, final JMatrix channel) {
        JVectorSequence data = new IterableJVectorSequence(dataMat.rowsIterator(), true);
        LOGGER.info("Evaluating frame level background");
        FrameLvlBgr bgr = ubm.evalFrameLvlBgr(data);
        final JVectorSequence fixedData;
        if (channel != null) {
            LOGGER.info("Training channel factors");
            EigenMapTrain channelTrain = new EigenMapTrain(channel, ubm);
            channelTrain.setData(data, bgr);
            channelTrain.channelEMIteration();
            SuperVector ux = channelTrain.getFeatureUx();
            LOGGER.info("Performing feature space channel compensation");
            data.reset();
            int i = 0;
            List<FloatVector> fixedDataList = new ArrayList<FloatVector>();
            JVector dataVector = data.getNextVector();
            while (dataVector != null) {
                int top1 = bgr.getMixtureVector(i++)[0];
                JVector uxpart = ux.subVectors[top1];
                JVector x = dataVector.minus(uxpart);
                fixedDataList.add(DenseFactory.floatVector(x.toDoubles()));
                dataVector = data.getNextVector();
            }
            fixedData = new IterableJVectorSequence(fixedDataList);
        } else {
            fixedData = data;
        }
        LOGGER.info("Training speaker model");
        EigenMapTrain speakerTrain = new EigenMapTrain(null, ubm);
        speakerTrain.setData(fixedData, bgr);
        for (int iter = 1; iter <= MAP_ITERATIONS; iter++) {
            double ll = speakerTrain.EMIteration(false);
            LOGGER.info("MAP iteration {}, log likelihood = {}", iter, ll);
        }
        return speakerTrain.getSModel().data;
    }
}
