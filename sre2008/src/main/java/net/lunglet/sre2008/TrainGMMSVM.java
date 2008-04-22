package net.lunglet.sre2008;

import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import java.io.File;
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
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.array4j.matrix.packed.FloatPackedMatrix;
import net.lunglet.array4j.matrix.packed.PackedFactory;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.TrainSVM.HDFHandle;
import net.lunglet.sre2008.svm.SpeakerKernelMatrix;
import net.lunglet.svm.Handle;
import net.lunglet.svm.SvmClassifier;
import net.lunglet.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO store uuid as attribute in each gmm and svm
// make svm refer to uuid of gmm
// also make svm refer to uuid of kernel
// make kernel refer to uuids of its data, in order

public class TrainGMMSVM {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainGMMSVM.class);

    public static void main(final String[] args) throws IOException, InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        if (args.length != 5) {
            // TODO make kernel optional. if it is not provided, calculate it.
            LOGGER.error("Usage: TrainGMMSVM MFCCLIST UBM CHANNEL BACKGROUND KERNEL");
            System.exit(1);
        }
        List<String> names = TrainGMM3.readFilelist(args[0]);
        JMapGMM ubm = TrainGMM3.readUBM( args[1]);
        JMatrix channel = TrainGMM3.readChannel(args[2]);
        List<Handle> backgroundData = readBackgroundData(args[3]);
        FloatMatrix kernel = readKernel(args[4], backgroundData.size());

        final int threads = 4;
        final int queueCapacity = 10 * threads;
        ExecutorService executorService = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            train(names, ubm, channel, backgroundData, kernel, executorService);
        } finally {
            LOGGER.info("Shutting down");
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }

    private static List<Handle> readBackgroundData(final String filename) throws IOException {
        List<String> names = TrainGMM3.readFilelist(filename);
        List<Handle> bgdata = new ArrayList<Handle>();
        int index = 0;
        for (String name : names) {
            // use label 1 here to make signs come out right
            bgdata.add(new HDFHandle(name, "/gmm", index++, 1));
        }
        return Collections.unmodifiableList(bgdata);
    }

    private static FloatMatrix readKernel(final String filename, final int dim) {
        LOGGER.info("Reading kernel from {}", filename);
        HDFReader kernelReader = new HDFReader(filename, 0);
        FloatPackedMatrix kernel = PackedFactory.floatSymmetricDirect(dim);
        kernelReader.read("/kernel", kernel);
        kernelReader.close();
        return kernel;
    }

    private static void train(final List<String> names, final JMapGMM ubm, final JMatrix channel,
            final List<Handle> backgroundData, final FloatMatrix background, final ExecutorService executorService) {
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
                        String outputName = name + "." + j + ".gmmsvm";
                        if (false && new File(outputName).exists()) {
                            return null;
                        }
                        LOGGER.info("Reading {} from {}", hdfName, name);
                        H5File h5file2 = new H5File(name);
                        FloatDenseMatrix data = DenseFactory.floatRowHeap(dims[0], dims[1]);
                        synchronized (reader) {
                            reader.read(h5file2, hdfName, data);
                        }
                        h5file2.close();
                        FloatDenseVector gmmsv = DenseFactory.floatVector(TrainGMM3.trainGMM(data, ubm, channel));
                        FloatDenseVector svmsv = DenseFactory.floatVector(trainSVM(background, gmmsv, backgroundData));
                        AssertUtils.assertTrue(svmsv.length() == gmmsv.length() + 1);
                        LOGGER.info("Writing GMM and SVM to {}", outputName);
                        HDFWriter writer = new HDFWriter(outputName);
                        writer.write("/gmm", gmmsv);
                        writer.write("/svm", svmsv);
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

    private static float[] trainSVM(final FloatMatrix background, final FloatVector sv,
            final List<Handle> backgroundData) {
        // do remaining kernel evaluations
        FloatVector speaker = DenseFactory.floatColumn(backgroundData.size() + 1);
        for (int i = 0; i < backgroundData.size(); i++) {
            FloatVector x = backgroundData.get(i).getData();
            speaker.set(i, FloatMatrixMath.dot(sv, x));
        }
        speaker.set(speaker.length() - 1, FloatMatrixMath.dot(sv, sv));

        // create complete kernel matrix
        SpeakerKernelMatrix kernel = new SpeakerKernelMatrix(background, speaker);

        final int index = backgroundData.size();
        List<Handle> svmData = new ArrayList<Handle>(backgroundData);
        svmData.add(new Handle() {
            @Override
            public FloatVector getData() {
                return sv;
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public int getLabel() {
                // use label 0 here to make signs come out right
                return 0;
            }
        });
        SvmClassifier svm = new SvmClassifier(svmData, kernel);
        svm.train(1000.0);
        SvmClassifier compactSvm = svm.compact();
        FloatVector model = compactSvm.getModel();

        // check scores
        float targetScore = compactSvm.score(sv).get(0, 0);
        AssertUtils.assertTrue(targetScore > 0);
        float[] scores = compactSvm.score(backgroundData).toArray();
        for (int i = 0; i < scores.length - 1; i++) {
            AssertUtils.assertTrue(scores[i] < 0);
        }

        // Z-Norm using background data (might need other data)
        double[] params = Evaluation.getParams(scores);
        double mean = params[0];
        double stddev = params[1];
        double rho = model.get(model.length() - 1);
        model.set(model.length() - 1, (float) (rho + mean));
        // scale model by 1/stddev
        for (int i = 0; i < model.length(); i++) {
            model.set(i, (float) (model.get(i) / stddev));
        }

        return model.toArray();
    }
}
