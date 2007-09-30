package net.lunglet.lre.lre07;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import net.lunglet.hdf.H5File;
import net.lunglet.svm.jacksvm.H5KernelReader2;
import net.lunglet.svm.jacksvm.Handle2;
import net.lunglet.svm.jacksvm.JackSVM2;
import net.lunglet.svm.jacksvm.Handle2.Score;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JackKnifeSVM {
    private static final Log LOG = LogFactory.getLog(JackKnifeSVM.class);
    
    private final CrossValidationSplits cvsplits;
    
    private final H5File datah5;
    
    private final H5File kernelh5;
    
    public JackKnifeSVM(final CrossValidationSplits cvsplits, final H5File datah5, final H5File kernelh5) {
        this.cvsplits = cvsplits;
        this.datah5 = datah5;
        this.kernelh5 = kernelh5;
    }

    public Map<String, JackSVM2> trainModels() throws IOException, InterruptedException, ExecutionException {
        Map<String, JackSVM2> models = new HashMap<String, JackSVM2>();
        LOG.info("reading kernel");
        final H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        Map<String, Handle2> frontendData = cvsplits.getFrontendData(datah5);
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                final String modelName = "frontend_" + ts + "_" + bs;
                final List<Handle2> trainData = cvsplits.getData(modelName, frontendData);
                JackSVM2 svm = new JackSVM2(kernelReader);
                svm.train(trainData);
                svm.compact();
                models.put(modelName, svm);
            }
        }
        return models;
    }

//    private static void scoreBackend(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
//        JackSVM2 firstModel = models.get("frontend_0_0");
//        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
//        List<String> targetLabels = firstModel.getTargetLabels();
//        for (int ts = 0; ts < TEST_SPLITS; ts++) {
//            FloatDenseMatrix backendSVs = new FloatDenseMatrix(firstSVs.rows(), firstSVs.columns(),
//                Orientation.COLUMN, Storage.DIRECT);
//            FloatDenseVector backendRhos = new FloatDenseVector(firstSVs.rows());
//            for (int bs = 0; bs < BACKEND_SPLITS; bs++) {
//                String modelName = "frontend_" + ts + "_" + bs;
//                JackSVM2 svm = models.get(modelName);
//                String splitName = "backend_" + ts + "_" + bs;
//                List<Handle2> data = readSplit(splitName, datah5);
//                LOG.info("scoring " + data.size() + " backend segments");
//                svm.score(data);
//                LOG.info("scoring done");
//                writeScores(splitName, data);
//                FloatMatrixMath.plusEquals(backendSVs, svm.getSupportVectors());
//                FloatMatrixMath.plusEquals(backendRhos, svm.getRhos());
//                if (!targetLabels.equals(svm.getTargetLabels())) {
//                    throw new AssertionError();
//                }
//            }
//            backendSVs.divideEquals(BACKEND_SPLITS);
//            backendRhos.divideEquals(BACKEND_SPLITS);
//            models.put("frontend_" + ts, new JackSVM2(backendSVs, backendRhos, targetLabels));
//        }
//    }

//    private static void scoreTest(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
//        JackSVM2 firstModel = models.get("frontend_0");
//        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
//        List<String> targetLabels = firstModel.getTargetLabels();
//        final int rows = firstSVs.rows();
//        final int cols = firstSVs.columns();
//        FloatDenseMatrix finalSVs = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
//        FloatDenseVector finalRhos = new FloatDenseVector(rows);
//        for (int ts = 0; ts < TEST_SPLITS; ts++) {
//            String modelName = "frontend_" + ts;
//            JackSVM2 svm = models.get(modelName);
//            String splitName = "test_" + ts;
//            List<Handle2> data = readSplit(splitName, datah5);
//            LOG.info("scoring " + data.size() + " test segments");
//            svm.score(data);
//            LOG.info("scoring done");
//            writeScores(splitName, data);
//            FloatMatrixMath.plusEquals(finalSVs, svm.getSupportVectors());
//            FloatMatrixMath.plusEquals(finalRhos, svm.getRhos());
//            if (!targetLabels.equals(svm.getTargetLabels())) {
//                throw new AssertionError();
//            }
//        }
//        finalSVs.divideEquals(TEST_SPLITS);
//        finalRhos.divideEquals(TEST_SPLITS);
//        models.put("final", new JackSVM2(finalSVs, finalRhos, targetLabels));
//    }

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
        String workingDir = Constants.WORKING_DIRECTORY;
        H5File datah5 = new H5File(new File(workingDir, "czngrams.h5"), H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File(new File(workingDir, "czngrams_kernel.h5"), H5File.H5F_ACC_RDONLY);
        CrossValidationSplits cvsplits = new CrossValidationSplits(10, 10);
        JackKnifeSVM jacksvm = new JackKnifeSVM(cvsplits, datah5, kernelh5);
        LOG.info("training frontend models");
        Map<String, JackSVM2> models = jacksvm.trainModels();
        LOG.info("training done");
        kernelh5.close();
//        scoreBackend(models, datah5);
//        scoreTest(models, datah5);
//        // TODO score final model on everything as a check
        datah5.close();
        LOG.info("done");
    }
}
