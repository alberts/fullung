package net.lunglet.lre.lre07;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.svm.jacksvm.AbstractHandle2;
import net.lunglet.svm.jacksvm.H5KernelReader2;
import net.lunglet.svm.jacksvm.Handle2;
import net.lunglet.svm.jacksvm.JackSVM2;
import net.lunglet.svm.jacksvm.Handle2.Score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;

public final class JackKnifeSVM {
    private static final int TEST_SPLITS = 10;

    private static final int BACKEND_SPLITS = 10;

    private static final Log LOG = LogFactory.getLog(JackKnifeSVM.class);

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

    private static final List<String> readNames(final String splitName, final H5File datah5) throws IOException {
        String fileName = "C:/home/albert/LRE2007/keysetc/albert/output/" + splitName + ".txt";
        LOG.info("reading " + fileName);
        List<String> names = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            final String label = parts[0];
            String[] idparts = parts[1].split(",");
            final String name = "/" + idparts[0] + "/" + idparts[1];
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            String label2 = ds.getStringAttribute("label");
            ds.close();
            if (!label.equals(label2)) {
                throw new AssertionError();
            }
            names.add(name);
            line = reader.readLine();
        }
        reader.close();
        return names;
    }

    private static final List<Handle2> readSplit(final String splitName, final H5File datah5) throws IOException {
        return readData(readNames(splitName, datah5), datah5);
    }

    private static Map<String, JackSVM2> trainModels(final H5File datah5, final H5File kernelh5) throws IOException,
            InterruptedException, ExecutionException {
        Map<String, JackSVM2> models = new HashMap<String, JackSVM2>();
        LOG.info("reading kernel");
        final H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        int nThreads = 2;
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(2 * nThreads), new ThreadPoolExecutor.CallerRunsPolicy());
        CompletionService<Object[]> completionService = new ExecutorCompletionService<Object[]>(threadPool);
        for (int ts = 0; ts < TEST_SPLITS; ts++) {
            for (int bs = 0; bs < BACKEND_SPLITS; bs++) {
                final String modelName = "frontend_" + ts + "_" + bs;
                completionService.submit(new Callable<Object[]>() {
                    @Override
                    public Object[] call() throws Exception {
                        final List<Handle2> trainData;
                        synchronized (datah5) {
                            trainData = readSplit(modelName, datah5);
                        }
                        JackSVM2 svm = new JackSVM2(kernelReader);
                        svm.train(trainData);
                        synchronized (datah5) {
                            svm.compact();
                        }
                        return new Object[]{modelName, svm};
                    }
                });
            }
        }
        for (int i = 0; i < TEST_SPLITS * BACKEND_SPLITS; i++) {
            Future<Object[]> future = completionService.take();
            Object[] result = future.get();
            models.put((String) result[0], (JackSVM2) result[1]);
        }
        LOG.info("shutting down thread pool");
        threadPool.shutdown();
        threadPool.awaitTermination(0L, TimeUnit.MILLISECONDS);
        return models;
    }

    private static void scoreBackend(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        JackSVM2 firstModel = models.get("frontend_0_0");
        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
        List<String> targetLabels = firstModel.getTargetLabels();
        for (int ts = 0; ts < TEST_SPLITS; ts++) {
            FloatDenseMatrix backendSVs = new FloatDenseMatrix(firstSVs.rows(), firstSVs.columns(),
                Orientation.COLUMN, Storage.DIRECT);
            FloatDenseVector backendRhos = new FloatDenseVector(firstSVs.rows());
            for (int bs = 0; bs < BACKEND_SPLITS; bs++) {
                String modelName = "frontend_" + ts + "_" + bs;
                JackSVM2 svm = models.get(modelName);
                String splitName = "backend_" + ts + "_" + bs;
                List<Handle2> data = readSplit(splitName, datah5);
                LOG.info("scoring " + data.size() + " backend segments");
                svm.score(data);
                LOG.info("scoring done");
                writeScores(splitName, data);
                FloatMatrixMath.plusEquals(backendSVs, svm.getSupportVectors());
                FloatMatrixMath.plusEquals(backendRhos, svm.getRhos());
                if (!targetLabels.equals(svm.getTargetLabels())) {
                    throw new AssertionError();
                }
            }
            backendSVs.divideEquals(BACKEND_SPLITS);
            backendRhos.divideEquals(BACKEND_SPLITS);
            models.put("frontend_" + ts, new JackSVM2(backendSVs, backendRhos, targetLabels));
        }
    }

    private static void scoreTest(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        JackSVM2 firstModel = models.get("frontend_0");
        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
        List<String> targetLabels = firstModel.getTargetLabels();

        FloatDenseMatrix finalSVs = new FloatDenseMatrix(firstSVs.rows(), firstSVs.columns(), Orientation.COLUMN,
                Storage.DIRECT);
        FloatDenseVector finalRhos = new FloatDenseVector(firstSVs.rows());

        for (int ts = 0; ts < TEST_SPLITS; ts++) {
            String modelName = "frontend_" + ts;
            JackSVM2 svm = models.get(modelName);
            String splitName = "test_" + ts;
            List<Handle2> data = readSplit(splitName, datah5);
            LOG.info("scoring " + data.size() + " test segments");
            svm.score(data);
            LOG.info("scoring done");
            writeScores(splitName, data);
            FloatMatrixMath.plusEquals(finalSVs, svm.getSupportVectors());
            FloatMatrixMath.plusEquals(finalRhos, svm.getRhos());
            if (!targetLabels.equals(svm.getTargetLabels())) {
                throw new AssertionError();
            }
        }
        finalSVs.divideEquals(TEST_SPLITS);
        finalRhos.divideEquals(TEST_SPLITS);
        models.put("final", new JackSVM2(finalSVs, finalRhos, targetLabels));
    }

    private static void writeScores(final String splitName, final List<Handle2> data) throws IOException {
        String fileName = splitName + ".scores.txt";
        LOG.info("writing scores to " + fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (Handle2 handle : data) {
            List<Score> scores = handle.getScores();
            Collections.sort(scores);
            StringBuilder lineBuilder = new StringBuilder();
            String[] parts = handle.getName().substring(1).split("/");
            lineBuilder.append(parts[0] + "," + parts[1]);
            lineBuilder.append(" ");
            lineBuilder.append(handle.getLabel());
            lineBuilder.append(" ");
            for (Score score : scores) {
                lineBuilder.append(score.getScore());
                lineBuilder.append(" ");
            }
            lineBuilder.append("\n");
            writer.write(lineBuilder.toString());
        }
        writer.close();
    }

    public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException {
        LOG.info("starting");
        H5File datah5 = new H5File("F:/ngrams.h5", H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File("F:/ngrams_kernel.h5", H5File.H5F_ACC_RDONLY);
        LOG.info("training frontend models");
        Map<String, JackSVM2> models = trainModels(datah5, kernelh5);
        LOG.info("training done");
        kernelh5.close();
        scoreBackend(models, datah5);
        scoreTest(models, datah5);
        // TODO score final model on everything as a check
        datah5.close();
        LOG.info("done");
    }
}
