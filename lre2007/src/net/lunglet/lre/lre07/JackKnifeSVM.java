package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.io.HDFReader;
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
import java.util.Set;
import java.util.concurrent.ExecutionException;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;
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
        Map<String, Handle2> frontendHandles = cvsplits.getDataMap("frontend", datah5);
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                final String modelName = "frontend_" + ts + "_" + bs;
                final List<Handle2> trainData = cvsplits.getData(modelName, frontendHandles);
                JackSVM2 svm = new JackSVM2(kernelReader);
                svm.train(trainData);
                svm.compact();
                models.put(modelName, svm);
            }
        }
        return models;
    }

    private static final int SV_DIM = 19182;

    private static FloatDenseMatrix readData(final List<SplitEntry> entries) {
        final int rows = SV_DIM + 1;
        final int cols = entries.size();
        H5File datah5 = new H5File("G:/czngrams.h5", H5File.H5F_ACC_RDONLY);
        FloatDenseMatrix data = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
        for (int i = 0; i < entries.size(); i++) {
            String name = entries.get(i).getName();
            FloatDenseVector x = data.column(i);
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            DataSpace memSpace = new DataSpace(x.length() - 1);
            DataSpace fileSpace = ds.getSpace();
            long[] start = new long[]{0, 0};
            long[] count = new long[]{1, 1};
            long[] block = new long[]{1, x.length() - 1};
            fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
            ds.read(x.data(), FloatType.IEEE_F32LE, memSpace, fileSpace);
            fileSpace.close();
            memSpace.close();
            ds.close();
        }
        // fill last row with 1's so that -rho is added to the scores
        FloatMatrixUtils.fill(data.row(data.rows() - 1), 1.0f);
        datah5.close();
        return data;
    }

    private void writeScores(List<SplitEntry> entries, final FloatDenseMatrix scores, final String filename)
            throws IOException {
        LOG.info("writing scores to " + filename);
        if (entries.size() != scores.columns()) {
            throw new IllegalArgumentException();
        }
        List<String> lines = new ArrayList<String>();
        for (int i = 0; i < entries.size(); i++) {
            StringBuilder lineBuilder = new StringBuilder();
            SplitEntry entry = entries.get(i);
            String id = String.format("%d/%s/%s", entry.getDuration(), entry.getCorpus(), entry.getBaseName());
            lineBuilder.append(id);
            lineBuilder.append(" ");
            lineBuilder.append(entry.getLanguage());
            lineBuilder.append(" ");
            for (int j = 0; j < scores.rows(); j++) {
                lineBuilder.append(String.format("%.15f", scores.get(j, i)));
                lineBuilder.append(" ");
            }
            lineBuilder.append("\n");
            lines.add(lineBuilder.toString());
        }
        Collections.sort(lines);
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename), 1024 * 1024);
        for (String line : lines) {
            writer.write(line);
        }
        writer.close();
    }

    public void scoreBackend2(final Map<String, FloatDenseMatrix> models, final H5File datah5) throws IOException {
        H5File modelsh5 = new H5File("G:/czmodels.h5", H5File.H5F_ACC_RDONLY);
        for (int tidx = 0; tidx < cvsplits.getTestSplits(); tidx++) {
            for (int beidx = 0; beidx < cvsplits.getBackendSplits(); beidx++) {
                String modelName = "frontend_" + tidx + "_" + beidx;
                HDFReader reader = new HDFReader(modelsh5);
                FloatDenseMatrix model = new FloatDenseMatrix(14, SV_DIM + 1, Orientation.ROW, Storage.DIRECT);
                reader.read(modelName, model);
//                FloatDenseMatrix model2 = models.get(modelName);
//                System.out.println(model.row(0));
//                System.out.println(model2.row(0));
                Set<SplitEntry> besplit = cvsplits.getSplit("backend_" + tidx + "_" + beidx);
                List<SplitEntry> besplitList = new ArrayList<SplitEntry>(besplit);
                FloatDenseMatrix backend = readData(besplitList);
                FloatDenseMatrix scores = FloatMatrixMath.times(model, backend);
                writeScores(besplitList, scores, "grid.backend." + tidx + "." + beidx + ".scores.txt");
            }
        }
        modelsh5.close();
    }

    public void scoreBackend(final Map<String, JackSVM2> models, final H5File datah5) throws IOException {
        JackSVM2 firstModel = models.get("frontend_0_0");
        FloatDenseMatrix firstSVs = firstModel.getSupportVectors();
        List<String> targetLabels = firstModel.getTargetLabels();
        for (int ts = 0; ts < cvsplits.getTestSplits(); ts++) {
            FloatDenseMatrix backendSVs = new FloatDenseMatrix(firstSVs.rows(), firstSVs.columns(),
                Orientation.COLUMN, Storage.DIRECT);
            FloatDenseVector backendRhos = new FloatDenseVector(firstSVs.rows());
            for (int bs = 0; bs < cvsplits.getBackendSplits(); bs++) {
                String modelName = "frontend_" + ts + "_" + bs;
                JackSVM2 svm = models.get(modelName);
                String splitName = "backend_" + ts + "_" + bs;
                List<Handle2> data = cvsplits.getData(splitName, datah5);
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
            backendSVs.divideEquals(cvsplits.getBackendSplits());
            backendRhos.divideEquals(cvsplits.getBackendSplits());
            models.put("frontend_" + ts, new JackSVM2(backendSVs, backendRhos, targetLabels));
        }
    }

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
        List<String> lines = new ArrayList<String>();
        for (Handle2 handle : data) {
            List<Score> scores = new ArrayList<Score>(handle.getScores());
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
        H5File kernelh5 = new H5File(new File(workingDir, "czngrams_kernel.h5"), H5File.H5F_ACC_RDONLY);
        CrossValidationSplits cvsplits = new CrossValidationSplits(1, 1);
        JackKnifeSVM jacksvm = new JackKnifeSVM(cvsplits, datah5, kernelh5);
        LOG.info("training frontend models");
        Map<String, JackSVM2> models = jacksvm.trainModels();
        Map<String, FloatDenseMatrix> models2 = new HashMap<String, FloatDenseMatrix>();
        for (Map.Entry<String, JackSVM2> entry : models.entrySet()) {
            models2.put(entry.getKey(), entry.getValue().getModels());
        }
        LOG.info("training done");
        kernelh5.close();
        jacksvm.scoreBackend(models, datah5);
        jacksvm.scoreBackend2(models2, datah5);
//        scoreTest(models, datah5);
//        // TODO score final model on everything as a check
        datah5.close();
        LOG.info("done");
    }
}
