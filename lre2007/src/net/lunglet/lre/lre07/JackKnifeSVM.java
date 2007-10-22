package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.io.HDFReader;
import com.googlecode.array4j.math.FloatMatrixMath;
import com.googlecode.array4j.util.AssertUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.H5File;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;
import net.lunglet.svm.jacksvm.CompactJackSVM2Builder;
import net.lunglet.svm.jacksvm.H5KernelReader2;
import net.lunglet.svm.jacksvm.H5NAPKernelReader;
import net.lunglet.svm.jacksvm.Handle2;
import net.lunglet.svm.jacksvm.JackSVM2;
import net.lunglet.svm.jacksvm.KernelReader;
import net.lunglet.svm.jacksvm.Score;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JackKnifeSVM {
    private static final Log LOG = LogFactory.getLog(JackKnifeSVM.class);

    private static final boolean NAP_ENABLED = true;

    private static final int NTHREADS = Math.max(8, Constants.CVSPLITS.getBackendSplits());

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(NTHREADS);

    private final CrossValidationSplits cvsplits;

    private final H5File datah5;

    private final H5File kernelh5;

    public JackKnifeSVM(final CrossValidationSplits cvsplits, final H5File datah5, final H5File kernelh5) {
        this.cvsplits = cvsplits;
        this.datah5 = datah5;
        this.kernelh5 = kernelh5;
    }

    private Map<String, JackSVM2> compact(final Map<String, CompactJackSVM2Builder> svmBuilders,
            final Map<String, Handle2> trainDataMap) throws InterruptedException, ExecutionException {
        LOG.info("presenting data to compact svm builders");
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        // present handles in sorted order
        // TODO only read data that are needed by at least one model
        List<Handle2> handles = new ArrayList<Handle2>(trainDataMap.values());
        Collections.sort(handles);
        for (Handle2 handle : handles) {
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
            List<Future<Void>> futures = EXECUTOR.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
            tasks.clear();
        }
        LOG.info("building compacted svms");
        Map<String, JackSVM2> models = new HashMap<String, JackSVM2>();
        for (Map.Entry<String, CompactJackSVM2Builder> entry : svmBuilders.entrySet()) {
            String modelName = entry.getKey();
            JackSVM2 svm = entry.getValue().build();
            models.put(modelName, svm);
        }
        return models;
    }

    public Map<String, JackSVM2> trainModels(final H5File naph5) throws IOException, InterruptedException,
            ExecutionException {
        final Map<String, CompactJackSVM2Builder> svmBuilders = new HashMap<String, CompactJackSVM2Builder>();
        final Map<String, FloatDenseMatrix> napMap = new HashMap<String, FloatDenseMatrix>();
        LOG.info("reading kernel");
        final H5KernelReader2 origKernelReader;
        final H5NAPKernelReader napKernelReader;
        if (naph5 == null) {
            origKernelReader = new H5KernelReader2(kernelh5);
            napKernelReader = null;
        } else {
            origKernelReader = null;
            napKernelReader = new H5NAPKernelReader(kernelh5);
        }
        LOG.info("reading data map");
        // get map of handles that discard their data after every read
        final Map<String, Handle2> trainDataMap = cvsplits.getDataMap("frontend", datah5);
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            final KernelReader kernelReader;
            if (napKernelReader != null) {
                String napDsName = "/nap_" + ts;
                LOG.info("reading dataset " + napDsName + " to get nap vectors");
                DataSet napds = naph5.getRootGroup().openDataSet(napDsName);
                DataSpace napspace = napds.getSpace();
                long[] napdims = napspace.getDims();
                final int napdim0;
                final int napdim1;
                if (napdims.length == 1) {
                    napdim0 = (int) napdims[0];
                    napdim1 = 1;
                } else {
                    AssertUtils.assertEquals(2, napdims.length);
                    napdim0 = (int) napdims[0];
                    napdim1 = (int) napdims[1];
                }
                napspace.close();
                napds.close();
                FloatDenseMatrix nap = new FloatDenseMatrix(napdim0, napdim1, Orientation.ROW, Storage.DIRECT);
                new HDFReader(naph5).read(napDsName, nap);
                napMap.put("nap_" + ts, nap);
                LOG.info("preparing napped kernel using nap_" + ts + " with shape = " + Arrays.toString(nap.shape()));
                List<Handle2> allTrainData = cvsplits.getData("frontend_" + ts, trainDataMap);
                kernelReader = napKernelReader.getKernelReader(allTrainData, nap);
            } else {
                kernelReader = origKernelReader;
            }
            // manage the futures in the inner loop so that we don't make too many kernels
            List<Future<?>> futures = new ArrayList<Future<?>>();
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                final String trainDataSplitName = "frontend_" + ts + "_" + bs;
                final String modelName = "backend_" + ts + "_" + bs;
                Future<?> future = EXECUTOR.submit(new Runnable() {
                    public void run() {
                        List<Handle2> trainData = cvsplits.getData(trainDataSplitName, trainDataMap);
                        JackSVM2 svm = new JackSVM2(kernelReader);
                        LOG.info("training model to score " + modelName + " on " + trainData.size() + " supervectors");
                        svm.train(trainData);
                        synchronized (svmBuilders) {
                            svmBuilders.put(modelName, svm.getCompactBuilder());
                            LOG.info(svmBuilders.size() + " models trained");
                        }
                    }
                });
                futures.add(future);
            }
            LOG.info("getting futures of training tasks");
            for (Future<?> future : futures) {
                future.get();
            }
            LOG.info("got all training task futures");
            futures.clear();
        }

        Map<String, JackSVM2> models = compact(svmBuilders, trainDataMap);
        if (naph5 == null) {
            return models;
        }
        final Orientation orient = Orientation.COLUMN;
        final Storage storage = Storage.DIRECT;
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            FloatDenseMatrix nap = napMap.get("nap_" + ts);
            FloatDenseMatrix napTrans = nap.transpose();
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                String modelName = "backend_" + ts + "_" + bs;
                LOG.info("applying nap transform to " + modelName + " model");
                JackSVM2 svm = models.get(modelName);
                // get transposed support vectors, because they are stored as rows
                FloatDenseMatrix sv = svm.getSupportVectors().transpose();
                FloatDenseMatrix temp = new FloatDenseMatrix(napTrans.rows(), sv.columns(), orient, storage);
                FloatDenseBLAS.DEFAULT.gemm(1.0f, napTrans, sv, 0.0f, temp);
                FloatDenseBLAS.DEFAULT.gemm(-1.0f, nap, temp, 1.0f, sv);
            }
        }
        return models;
    }

    private static void writeScore(final BufferedWriter writer, final Handle2 handle, final List<Score> scores)
            throws IOException {
        List<Score> sortedScores = new ArrayList<Score>(scores);
        Collections.sort(sortedScores);
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
        for (Score score : sortedScores) {
            lineBuilder.append(score.getScore());
            lineBuilder.append(" ");
        }
        lineBuilder.append("\n");
        writer.write(lineBuilder.toString());
    }

    private void averageModels(final Map<String, JackSVM2> models) {
        JackSVM2 firstModel = models.get("backend_0_0");
        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
        List<String> targetLabels = firstModel.getTargetLabels();
        int rows = firstSVs.rows();
        int cols = firstSVs.columns();
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            FloatDenseMatrix backendSVs = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
            FloatDenseVector backendRhos = new FloatDenseVector(rows);
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                String modelName = "backend_" + ts + "_" + bs;
                JackSVM2 svm = models.get(modelName);
                FloatMatrixMath.plusEquals(backendSVs, svm.getSupportVectors());
                FloatMatrixMath.plusEquals(backendRhos, svm.getRhos());
                if (!targetLabels.equals(svm.getTargetLabels())) {
                    throw new AssertionError();
                }
            }
            // create models averaged over frontend splits
            backendSVs.divideEquals(cvsplits.getBackendSplits());
            backendRhos.divideEquals(cvsplits.getBackendSplits());
            JackSVM2 averageSvm = new JackSVM2(backendSVs, backendRhos, targetLabels);
            models.put("test_" + ts, averageSvm);
            models.put("sanity_" + ts, averageSvm);
            models.put("eval_" + ts, averageSvm);
        }
    }

    private static void addScoreWriter(final CrossValidationSplits cvsplits,
            final Map<String, BufferedWriter> scoreWriters, final String splitName, final String filename)
            throws IOException {
        Set<SplitEntry> split = cvsplits.getSplit(splitName);
        if (split != null && split.size() > 0) {
            scoreWriters.put(splitName, new BufferedWriter(new FileWriter(filename + ".wide"), 16384));
        }
    }

    public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException {
        LOG.info("starting");
        String workingDir = Constants.WORKING_DIRECTORY;
        H5File datah5 = new H5File(new File(workingDir, "czngrams.h5"), H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File(new File(workingDir, "czkernel.h5"), H5File.H5F_ACC_RDONLY);
        final H5File naph5;
        if (NAP_ENABLED) {
            naph5 = new H5File(new File(workingDir, "cznap.h5"), H5File.H5F_ACC_RDONLY);
        } else {
            naph5 = null;
        }
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        JackKnifeSVM jacksvm = new JackKnifeSVM(cvsplits, datah5, kernelh5);
        LOG.info("training frontend models");
        Map<String, JackSVM2> models = jacksvm.trainModels(naph5);
        if (naph5 != null) {
            naph5.close();
        }
        kernelh5.close();
        LOG.info("training done");
        LOG.info("averaging backend models -> test models");
        jacksvm.averageModels(models);
        // map from split name to writer for its scores
        LOG.info("creating score writers");
        Map<String, BufferedWriter> scoreWriters = new HashMap<String, BufferedWriter>();
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                addScoreWriter(cvsplits, scoreWriters, "backend_" + ts + "_" + bs, "back." + ts + "." + bs);
            }
            addScoreWriter(cvsplits, scoreWriters, "test_" + ts, "test." + ts);
//            addScoreWriter(cvsplits, scoreWriters, "sanity_" + ts, "sanity." + ts);
//            addScoreWriter(cvsplits, scoreWriters, "eval_" + ts, "eval." + ts);
        }
        LOG.info("scoring everything");
        List<SplitEntry> splitEntriesList = new ArrayList<SplitEntry>(cvsplits.getAllSplits());
        Collections.sort(splitEntriesList);
        for (SplitEntry splitEntry : splitEntriesList) {
            LOG.info("scoring " + splitEntry.getName());
            // get a handle that retains its data after the first read
            Handle2 handle = cvsplits.getData(splitEntry, datah5);
            for (Map.Entry<String, BufferedWriter> scoreWriterEntry : scoreWriters.entrySet()) {
                String splitName = scoreWriterEntry.getKey();
                JackSVM2 svm = models.get(splitName);
                Set<SplitEntry> writerSplit  = cvsplits.getSplit(splitName);
                if (writerSplit.contains(splitEntry)) {
                    List<Score> scores = svm.score(handle);
                    writeScore(scoreWriterEntry.getValue(), handle, scores);
                }
            }
        }
        LOG.info("closing score writers");
        for (BufferedWriter writer : scoreWriters.values()) {
            writer.close();
        }
        datah5.close();
        LOG.info("done. shutting down executor.");
        EXECUTOR.shutdown();
        EXECUTOR.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }
}
