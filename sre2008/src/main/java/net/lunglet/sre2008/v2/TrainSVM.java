package net.lunglet.sre2008.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.array4j.matrix.packed.FloatPackedMatrix;
import net.lunglet.array4j.matrix.packed.PackedFactory;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFUtils;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.HDFHandle;
import net.lunglet.sre2008.Model;
import net.lunglet.sre2008.Segment;
import net.lunglet.sre2008.svm.CalculateKernel;
import net.lunglet.sre2008.svm.SpeakerKernelMatrix;
import net.lunglet.svm.Handle;
import net.lunglet.svm.SvmClassifier;
import net.lunglet.util.ArrayMath;
import net.lunglet.util.AssertUtils;
import net.lunglet.util.MainTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

// TODO check that kernel matches background file using some kind of UUID scheme

public final class TrainSVM {
    @CommandLineInterface(application = "TrainSVM")
    private static interface Arguments {
        @Option(shortName = "b", description = "background data file")
        File getBackground();

        @Option(shortName = "i", description = "data file (input)")
        File getInput();

        @Option(shortName = "k", description = "kernel file")
        File getKernel();

        @Option(shortName = "o", description = "model file (output)")
        File getOutput();

        @Option(shortName = "t", description = "evaluation trn index")
        File getTrn();

        boolean isTrn();
    }

    public static final class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File backgroundFile = args.getBackground();
            checkFileExists("background data", backgroundFile);
            File kernelFile = args.getKernel();
            checkFileExists("kernel", kernelFile);
            File inputFile = args.getInput();
            checkFileExists("input", inputFile);
            File outputFile = args.getOutput();
            checkFileNotExists("output", outputFile);

            final List<Model> models;
            if (args.isTrn()) {
                LOGGER.info("Training evaluation models");
                checkFileExists("evaluation trn index", inputFile);
                File trnFile = args.getTrn();
                models = new ArrayList<Model>(Evaluation.readModels(trnFile).values());
            } else {
                LOGGER.info("Training TNorm models");
                models = new ArrayList<Model>();
                int i = 0;
                H5File inputh5 = new H5File(inputFile);
                for (DataSet dataset : HDFUtils.listDataSets(inputh5, HDFUtils.ALL_DATASETS_FILTER)) {
                    String name = dataset.getName();
                    models.add(new Model("tnorm" + i++, new Segment(name)));
                    dataset.close();
                }
                inputh5.close();
            }
            Collections.sort(models);
            checkData(models, inputFile);

            List<Handle> backgroundData = readBackgroundData(backgroundFile);
            FloatMatrix kernel = readKernel(kernelFile);
            if (kernel.rows() != backgroundData.size()) {
                throw new RuntimeException("Mismatch between background data and kernel");
            }

            ExecutorService executorService = createExecutorService();
            try {
                train(models, inputFile, outputFile, backgroundData, kernel, executorService);
            } finally {
                LOGGER.debug("Shutting down executor service");
                executorService.shutdown();
                executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
            }
            return 0;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainSVM.class);

    private static void checkData(final List<Model> models, final File dataFile) {
        H5File datah5 = new H5File(dataFile);
        for (Model model : models) {
            for (Segment train : model.getTrain()) {
                String hdfName = train.getHDFName();
                if (!datah5.getRootGroup().existsDataSet(hdfName)) {
                    throw new RuntimeException(hdfName + " for model " + model.getId() + " is missing from "
                            + datah5.getFileName());
                }
            }
        }
        datah5.close();
    }

    private static ExecutorService createExecutorService() {
        final int threads = 4;
        final int capacity = 10 * threads;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(capacity);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        return new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }

    private static List<Handle> readBackgroundData(final File backgroundFile) {
        LOGGER.info("Reading background data from {}", backgroundFile);
        List<Handle> temp = new ArrayList<Handle>();
        H5File bgh5 = new H5File(backgroundFile);
        List<String> names = CalculateKernel.getNames(bgh5);
        bgh5.close();
        int index = 0;
        for (String name : names) {
            // use label 1 here to make signs come out right
            temp.add(new HDFHandle(backgroundFile, name, index++, 1));
        }
        return Collections.unmodifiableList(temp);
    }

    private static FloatDenseVector readData(final HDFReader reader, final Model model) {
        String name = model.getTrain().get(0).getHDFName();
        DataSet dataset = reader.getH5File().getRootGroup().openDataSet(name);
        int[] dims = dataset.getIntDims();
        dataset.close();
        FloatDenseVector data = DenseFactory.floatRow(ArrayMath.max(dims));
        LOGGER.info("Loading data from {} {}", name, Arrays.toString(dims));
        reader.read(name, data);
        return data;
    }

    private static FloatMatrix readKernel(final File kernelFile) {
        HDFReader kernelReader = new HDFReader(kernelFile.getPath());
        DataSet kernelds = kernelReader.getH5File().getRootGroup().openDataSet("/kernel");
        int[] dims = kernelds.getIntDims();
        kernelds.close();
        int dim = (int) ((Math.sqrt(1.0 + 8.0 * dims[0]) - 1.0) / 2.0);
        AssertUtils.assertEquals(dims[0], dim * (dim + 1) / 2);
        LOGGER.info("Reading {} x {} kernel from {}", new Object[]{dim, dim, kernelFile});
        FloatPackedMatrix kernel = PackedFactory.floatSymmetricDirect(dim);
        kernelReader.read("/kernel", kernel);
        return kernel;
    }

    private static void train(final Collection<Model> models, final File inputFile, final File outputFile,
            final List<Handle> backgroundData, final FloatMatrix kernel, final ExecutorService executorService) {
        final HDFWriter writer = new HDFWriter(outputFile);
        final HDFReader reader = new HDFReader(inputFile, 16 * 1024 * 1024);
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        for (final Model model : models) {
            Future<Void> future = executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    final FloatVector modelSv;
                    synchronized (reader) {
                        modelSv = readData(reader, model);
                    }
                    float[] svmModel = trainModel(modelSv, backgroundData, kernel);
                    synchronized (writer) {
                        writer.write(model.getId(), DenseFactory.floatVector(svmModel));
                        writer.flush();
                    }
                    LOGGER.info("Wrote model for {}", model.getId());
                    return null;
                }
            });
            futures.add(future);
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
        writer.close();
    }

    private static float[] trainModel(final FloatVector modelSv, final List<Handle> backgroundData,
            final FloatMatrix background) {
        // do remaining kernel evaluations
        FloatVector speaker = DenseFactory.floatColumn(backgroundData.size() + 1);
        for (int i = 0; i < backgroundData.size(); i++) {
            FloatVector x = backgroundData.get(i).getData();
            speaker.set(i, FloatMatrixMath.dot(modelSv, x));
        }
        speaker.set(speaker.length() - 1, FloatMatrixMath.dot(modelSv, modelSv));

        // create complete kernel matrix
        SpeakerKernelMatrix kernel = new SpeakerKernelMatrix(background, speaker);

        final int index = backgroundData.size();
        List<Handle> svmData = new ArrayList<Handle>(backgroundData);
        svmData.add(new Handle() {
            @Override
            public FloatVector getData() {
                return modelSv;
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
        float targetScore = compactSvm.score(modelSv.transpose()).get(0, 0);
        // TODO throw FailureToEnrollException if these checks fail. log useful
        // info like which train segment and which test segment didn't work
        AssertUtils.assertTrue(targetScore > 0);
        float[] scores = compactSvm.score(backgroundData).toArray();
        for (int i = 0; i < scores.length - 1; i++) {
            AssertUtils.assertTrue(scores[i] < 0);
        }

        // Z-Norm using background data
        double[] params = ScoreUtils.getParams(scores);
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
