package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;

public final class PhonemeUtil {
    public static FloatDenseVector calculateNGrams(final FloatDenseMatrix posteriors) {
        if (posteriors.columns() < 2) {
            throw new IllegalArgumentException();
        }
        FloatDenseVector monograms = FloatMatrixUtils.columnSum(posteriors);
        monograms.plusEquals(1.0f);
        monograms.divideEquals(FloatMatrixUtils.sum(monograms));
        FloatMatrixMath.logEquals(monograms);
        monograms.minusEquals(FloatMatrixUtils.mean(monograms));

        FloatDenseMatrix b1 = FloatDenseUtils.subMatrixColumns(posteriors, 0, posteriors.columns() - 1);
        FloatDenseMatrix b2 = FloatDenseUtils.subMatrixColumns(posteriors, 1, posteriors.columns());
        FloatDenseMatrix bigrams = FloatMatrixMath.times(b1, b2.transpose());
        bigrams.plusEquals(1.0f);
        // TODO probably onnodig want die mean ding doen dit anyway
//        bigrams.divideEquals(FloatMatrixUtils.sum(bigrams));
        FloatMatrixMath.logEquals(bigrams);
        bigrams.minusEquals(FloatMatrixUtils.mean(bigrams));
        // TODO doen weer vektor normalisering
        FloatDenseVector bigramsVec = FloatMatrixUtils.columnsVector(bigrams);
        FloatDenseVector ngrams = FloatMatrixUtils.concatenate(monograms, bigramsVec);
        return ngrams;
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
