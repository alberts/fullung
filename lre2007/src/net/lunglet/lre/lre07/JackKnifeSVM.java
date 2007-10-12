package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.lunglet.hdf.H5File;
import net.lunglet.svm.jacksvm.CompactJackSVM2Builder;
import net.lunglet.svm.jacksvm.H5KernelReader2;
import net.lunglet.svm.jacksvm.Handle2;
import net.lunglet.svm.jacksvm.JackSVM2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JackKnifeSVM {
    private static final Log LOG = LogFactory.getLog(JackKnifeSVM.class);
    
    private static final int NTHREADS = 8;

    private final CrossValidationSplits cvsplits;

    private final H5File datah5;

    private final H5File kernelh5;

    public JackKnifeSVM(final CrossValidationSplits cvsplits, final H5File datah5, final H5File kernelh5) {
        this.cvsplits = cvsplits;
        this.datah5 = datah5;
        this.kernelh5 = kernelh5;
    }

    private Map<String, JackSVM2> compact(final ExecutorService executor,
            final Map<String, CompactJackSVM2Builder> svmBuilders, final Map<String, Handle2> trainDataMap)
            throws InterruptedException, ExecutionException {
        LOG.info("compacting");
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for (Handle2 handle : trainDataMap.values()) {
            final FloatVector<?> data = handle.getData();
            final int index = handle.getIndex();
            for (final CompactJackSVM2Builder svmBuilder : svmBuilders.values()) {
                tasks.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        svmBuilder.present(data, index);
                        return null;
                    }
                });
            }
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
            tasks.clear();
        }
        Map<String, JackSVM2> models = new HashMap<String, JackSVM2>();
        for (Map.Entry<String, CompactJackSVM2Builder> entry : svmBuilders.entrySet()) {
            String modelName = entry.getKey();
            JackSVM2 svm = entry.getValue().build();
            models.put(modelName, svm);
        }
        return models;
    }

    public Map<String, JackSVM2> trainModels() throws IOException, InterruptedException, ExecutionException {
        final Map<String, CompactJackSVM2Builder> svmBuilders = new HashMap<String, CompactJackSVM2Builder>();
        LOG.info("reading kernel");
        final H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        final Map<String, Handle2> trainDataMap = cvsplits.getDataMap("frontend", datah5);

        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                final String modelName = "frontend_" + ts + "_" + bs;
                final List<Handle2> trainData = cvsplits.getData(modelName, trainDataMap);
                Future<?> future = executor.submit(new Runnable() {
                    public void run() {
                        JackSVM2 svm = new JackSVM2(kernelReader);
                        LOG.info("training " + modelName);
                        svm.train(trainData);
                        synchronized (svmBuilders) {
                            svmBuilders.put(modelName, svm.getCompactBuilder());
                            LOG.info(svmBuilders.size() + " models trained");
                        }
                    }
                });
                futures.add(future);
            }
        }
        for (Future<?> future : futures) {
            future.get();
        }
        futures.clear();

        Map<String, JackSVM2> models = compact(executor, svmBuilders, trainDataMap);

        executor.shutdown();
        executor.awaitTermination(0L, TimeUnit.MILLISECONDS);

        return models;
    }

    public void scoreBackend(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        JackSVM2 firstModel = models.get("frontend_0_0");
        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
        List<String> targetLabels = firstModel.getTargetLabels();
        int rows = firstSVs.rows();
        int cols = firstSVs.columns();
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            FloatDenseMatrix backendSVs = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
            FloatDenseVector backendRhos = new FloatDenseVector(rows);
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                String modelName = "frontend_" + ts + "_" + bs;
                // remove model here because it won't be needed again
                JackSVM2 svm = models.remove(modelName);
                String splitName = "backend_" + ts + "_" + bs;
                List<Handle2> data = cvsplits.getData(splitName, datah5);
                LOG.info("scoring " + data.size() + " backend segments");
                svm.score(data);
                LOG.info("scoring done");
                writeScores("back." + ts + "." + bs, data);
                FloatMatrixMath.plusEquals(backendSVs, svm.getSupportVectors());
                FloatMatrixMath.plusEquals(backendRhos, svm.getRhos());
                if (!targetLabels.equals(svm.getTargetLabels())) {
                    throw new AssertionError();
                }
            }
            // create models averaged over frontend splits
            backendSVs.divideEquals(cvsplits.getBackendSplits());
            backendRhos.divideEquals(cvsplits.getBackendSplits());
            models.put("frontend_" + ts, new JackSVM2(backendSVs, backendRhos, targetLabels));
        }
    }

    public void scoreTest(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            String modelName = "frontend_" + ts;
            JackSVM2 svm = models.get(modelName);
            String splitName = "test_" + ts;
            List<Handle2> data = cvsplits.getData(splitName, datah5);
            LOG.info("scoring " + data.size() + " test segments");
            svm.score(data);
            LOG.info("scoring done");
            writeScores("test." + ts, data);
        }
    }

    public void scoreSanity(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            String modelName = "frontend_" + ts;
            JackSVM2 svm = models.get(modelName);
            List<Handle2> data = cvsplits.getData("test", datah5);
            LOG.info("scoring " + data.size() + " test segments (sanity check)");
            svm.score(data);
            LOG.info("scoring done");
            writeScores("sanity." + ts, data);
        }
    }

    public void scoreEval(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            String modelName = "frontend_" + ts;
            JackSVM2 svm = models.get(modelName);
            List<Handle2> data = cvsplits.getData("eval", datah5);
            LOG.info("scoring " + data.size() + " evaluation segments");
            svm.score(data);
            LOG.info("scoring done");
            writeScores("eval." + ts, data);
        }
    }

    private static void writeScores(final String splitName, final List<Handle2> data) throws IOException {
        String fileName = splitName + ".wide";
        LOG.info("writing scores to " + fileName);
        List<String> lines = new ArrayList<String>();
        for (Handle2 handle : data) {
            List<Handle2.Score> scores = new ArrayList<Handle2.Score>(handle.getScores());
            Collections.sort(scores);
            StringBuilder lineBuilder = new StringBuilder();
            String name = handle.getName();
            if (name.startsWith("/lid07e1/")) {
                String id = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf(".sph"));
                lineBuilder.append(id);
                lineBuilder.append(" unknown");
            } else {
                lineBuilder.append(handle.getDuration() + name.substring(0, name.lastIndexOf(".sph")));
                lineBuilder.append(" ");
                lineBuilder.append(handle.getLabel());
            }
            lineBuilder.append(" ");
            for (Handle2.Score score : scores) {
                lineBuilder.append(score.getScore());
                lineBuilder.append(" ");
            }
            lineBuilder.append("\n");
            lines.add(lineBuilder.toString());
        }
        Collections.sort(lines);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (String line : lines) {
            writer.write(line);
        }
        writer.close();
    }

    public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException {
        LOG.info("starting");
        String workingDir = Constants.WORKING_DIRECTORY;
        H5File datah5 = new H5File(new File(workingDir, "czngrams.h5"), H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File(new File(workingDir, "czkernel.h5"), H5File.H5F_ACC_RDONLY);
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        JackKnifeSVM jacksvm = new JackKnifeSVM(cvsplits, datah5, kernelh5);
        LOG.info("training frontend models");
        Map<String, JackSVM2> models = jacksvm.trainModels();
        LOG.info("training done");
        kernelh5.close();
        jacksvm.scoreBackend(models, datah5);
        jacksvm.scoreTest(models, datah5);
        jacksvm.scoreSanity(models, datah5);
        if (cvsplits.getSplit("eval") != null) {
            jacksvm.scoreEval(models, datah5);
        }
        datah5.close();
        LOG.info("done");
    }
}
