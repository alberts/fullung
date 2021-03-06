package cz.vutbr.fit.speech.phnrec;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public final class PhonemeUtil {
    public static FloatDenseVector calculateBigrams(final FloatDenseMatrix posteriors, final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
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

    public static FloatDenseVector calculateMonograms(final FloatDenseMatrix posteriors) {
        FloatDenseVector monograms = FloatMatrixUtils.columnSum(posteriors);
        monograms.plusEquals(1.0f);
        FloatMatrixMath.logEquals(monograms);
        monograms.minusEquals(FloatMatrixUtils.mean(monograms));
        return monograms;
    }

    public static FloatDenseVector calculateStaggeredBigrams(final FloatDenseMatrix posteriors, final int n) {
        FloatDenseVector[] parts = new FloatDenseVector[n];
        for (int i = 0; i < n; i++) {
            parts[i] = calculateBigrams(posteriors, i + 1);
        }
        return FloatMatrixUtils.concatenate(parts);
    }

    public static FloatDenseVector calculateTrigrams(final FloatDenseMatrix posteriors, final BitSet bigramIndexes) {
        if (posteriors.columns() < 3) {
            throw new UnsupportedOperationException();
        }
        final Orientation orient = Orientation.COLUMN;
        final Storage storage = Storage.DIRECT;
        FloatDenseVector bestBigrams1 = new FloatDenseVector(bigramIndexes.cardinality(), orient, storage);
        FloatDenseVector bestBigrams2 = new FloatDenseVector(bigramIndexes.cardinality(), orient, storage);
        final int m = bigramIndexes.cardinality();
        final int n = posteriors.rows();
        final FloatDenseMatrix bigramsMat1 = new FloatDenseMatrix(n, n, orient, storage);
        final FloatDenseMatrix bigramsMat2 = new FloatDenseMatrix(n, n, orient, storage);
        final FloatDenseVector bigramsVec1 = FloatMatrixUtils.columnsVector(bigramsMat1);
        final FloatDenseVector bigramsVec2 = FloatMatrixUtils.columnsVector(bigramsMat2);
        final FloatDenseMatrix trigramsMat1 = new FloatDenseMatrix(m, n, orient, storage);
        final FloatDenseMatrix trigramsMat2 = new FloatDenseMatrix(n, m, orient.transpose(), storage);
        final FloatDenseVector trigramsVec1 = FloatMatrixUtils.columnsVector(trigramsMat1);
        final FloatDenseVector trigramsVec2 = FloatMatrixUtils.columnsVector(trigramsMat2.transpose());
        final FloatDenseVector trigrams1 = new FloatDenseVector(trigramsVec1.length(), orient, storage);
        final FloatDenseVector trigrams2 = new FloatDenseVector(trigramsVec2.length(), orient, storage);
        for (int i = 0; i < posteriors.columns() - 2; i++) {
            FloatDenseVector x = posteriors.column(i);
            FloatDenseVector y = posteriors.column(i + 1);
            FloatDenseVector z = posteriors.column(i + 2);
            FloatDenseBLAS.DEFAULT.gemm(1.0f, x.asMatrix(), y.transpose().asMatrix(), 0.0f, bigramsMat1);
            FloatDenseBLAS.DEFAULT.gemm(1.0f, y.asMatrix(), z.transpose().asMatrix(), 0.0f, bigramsMat2);
            for (int j = 0, k = 0; j < bigramsVec1.length(); j++) {
                if (bigramIndexes.get(j)) {
                    bestBigrams1.set(k, bigramsVec1.get(j));
                    bestBigrams2.set(k, bigramsVec2.get(j));
                    k++;
                }
            }
            FloatDenseBLAS.DEFAULT.gemm(1.0f, bestBigrams1.asMatrix(), z.transpose().asMatrix(), 0.0f, trigramsMat1);
            FloatDenseBLAS.DEFAULT.gemm(1.0f, x.asMatrix(), bestBigrams2.transpose().asMatrix(), 0.0f, trigramsMat2);
            trigrams1.plusEquals(trigramsVec1);
            trigrams2.plusEquals(trigramsVec2);
        }
        FloatDenseVector trigrams = FloatMatrixUtils.concatenate(trigrams1, trigrams2);
        trigrams.plusEquals(1.0f);
        FloatMatrixMath.logEquals(trigrams);
        trigrams.minusEquals(FloatMatrixUtils.mean(trigrams));
        return trigrams;
    }

    public static BitSet getBigramIndexes(final String phonemePrefix, final int maxBigrams) throws IOException {
        InputStream stream = PhonemeUtil.class.getResourceAsStream(phonemePrefix + "bigrams.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = reader.readLine();
        List<String> lines = new ArrayList<String>();
        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();
        final float[] counts = new float[lines.size()];
        Integer[] indexes = new Integer[counts.length];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = Float.parseFloat(lines.get(i));
            indexes[i] = Integer.valueOf(i);
        }
        // sort indexes in descending order by counts
        Arrays.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return Float.compare(counts[i2], counts[i1]);
            }
        });
        BitSet bestIndexes = new BitSet(indexes.length);
        for (int i = 0; i < maxBigrams; i++) {
            bestIndexes.set(indexes[i]);
        }
        return bestIndexes;
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
