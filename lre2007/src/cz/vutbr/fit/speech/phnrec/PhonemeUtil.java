package cz.vutbr.fit.speech.phnrec;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class PhonemeUtil {
    private static FloatDenseVector bigrams(final FloatDenseMatrix posteriors, final int n) {
        if (posteriors.columns() <= n) {
            return new FloatDenseVector(posteriors.rows() * posteriors.rows());
        }
        FloatDenseMatrix b1 = FloatDenseUtils.subMatrixColumns(posteriors, 0, posteriors.columns() - n);
        FloatDenseMatrix b2 = FloatDenseUtils.subMatrixColumns(posteriors, n, posteriors.columns());
        FloatDenseMatrix bigrams = FloatMatrixMath.times(b1, b2.transpose());
        bigrams.plusEquals(1.0f);
        FloatMatrixMath.logEquals(bigrams);
        bigrams.minusEquals(FloatMatrixUtils.mean(bigrams));
        return FloatMatrixUtils.columnsVector(bigrams);
    }

    public static FloatDenseVector calculateNGrams(final FloatDenseMatrix posteriors) {
        FloatDenseVector monograms = FloatMatrixUtils.columnSum(posteriors);
        monograms.plusEquals(1.0f);
        monograms.divideEquals(FloatMatrixUtils.sum(monograms));
        FloatMatrixMath.logEquals(monograms);
        monograms.minusEquals(FloatMatrixUtils.mean(monograms));
        FloatDenseVector bigrams1 = bigrams(posteriors, 1);
        FloatDenseVector bigrams2 = bigrams(posteriors, 2);
//        FloatDenseVector bigrams3 = bigrams(posteriors, 3);
        return FloatMatrixUtils.concatenate(monograms, bigrams1, bigrams2);
    }

    public static List<MasterLabel> readMasterLabels(final Reader reader) throws IOException {
        List<MasterLabel> labels = new ArrayList<MasterLabel>();
        BufferedReader bufReader = new BufferedReader(reader);
        String line = bufReader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            String label = parts[2];
            long startTime = Long.valueOf(parts[0]);
            long endTime = Long.valueOf(parts[1]);
            float score = Float.valueOf(parts[3]);
            labels.add(new MasterLabel(label, startTime, endTime, score));
            line = bufReader.readLine();
        }
        // TODO maybe this close shouldn't be here
        bufReader.close();
        return labels;
    }

    private PhonemeUtil() {
    }
}
