package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.io.HDFReader;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.lre.lre07.Constants;
import net.lunglet.lre.lre07.CrossValidationSplits;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;

public final class MultipleSVMScorer {
    // TODO don't hard-code SV_DIM
    private static final int SV_DIM = 19182;

//    private static final String MODELS_FILENAME = "C:/home/albert/LRE2007/work/models.h5";
    private static final String MODELS_FILENAME = "G:/czmodels.h5";

//    private static final String DATA_FILENAME = "G:/data.h5";
    private static final String DATA_FILENAME = "G:/czngrams.h5";

    private static FloatDenseMatrix readData(final List<SplitEntry> entries) {
        final int rows = SV_DIM + 1;
        final int cols = entries.size();
        H5File datah5 = new H5File(DATA_FILENAME, H5File.H5F_ACC_RDONLY);
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

    private static void writeScores(List<SplitEntry> entries, final FloatDenseMatrix scores, final String filename)
            throws IOException {
        System.out.println(filename);
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

    public static void main(final String[] args) throws IOException {
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        H5File modelsh5 = new H5File(MODELS_FILENAME, H5File.H5F_ACC_RDONLY);
        for (int tidx = 0; tidx < cvsplits.getTestSplits(); tidx++) {
            for (int beidx = 0; beidx < cvsplits.getBackendSplits(); beidx++) {
                HDFReader reader = new HDFReader(modelsh5);
                FloatDenseMatrix model = new FloatDenseMatrix(14, SV_DIM + 1, Orientation.ROW, Storage.DIRECT);
                reader.read("frontend_" + tidx + "_" + beidx, model);
                Set<SplitEntry> besplit = cvsplits.getSplit("backend_" + tidx + "_" + beidx);
                List<SplitEntry> besplitList = new ArrayList<SplitEntry>(besplit);
                FloatDenseMatrix backend = readData(besplitList);
                FloatDenseMatrix scores = FloatMatrixMath.times(model, backend);
                writeScores(besplitList, scores, "backend." + tidx + "." + beidx + ".scores.txt");
            }
            Set<SplitEntry> testSplit = cvsplits.getSplit("test_" + tidx);
            List<SplitEntry> testSplitList = new ArrayList<SplitEntry>(testSplit);
            FloatDenseMatrix test = readData(testSplitList);

            // TODO use average model here
            FloatDenseMatrix model = new FloatDenseMatrix(14, SV_DIM + 1, Orientation.ROW, Storage.DIRECT);
            HDFReader reader = new HDFReader(modelsh5);
            reader.read("frontend_" + tidx + "_0", model);

            FloatDenseMatrix scores = FloatMatrixMath.times(model, test);
            writeScores(testSplitList, scores, "test." + tidx + ".scores.txt");
        }
        // TODO do another loop over tidx and score using avg-avg-model
        // TODO but write to a single file called test.txt here
        // TODO score frontend and backend too while we're at it
        modelsh5.close();
    }
}
