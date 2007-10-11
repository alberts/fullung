package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public final class TrigramSVCalculator implements SupervectorCalculator {
    private final BitSet bigramIndexes;

    public TrigramSVCalculator(final BitSet bigramIndexes) {
        this.bigramIndexes = bigramIndexes;
    }

    public FloatDenseVector apply(final FloatDenseMatrix posteriors) {
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
        return FloatMatrixUtils.concatenate(trigrams1, trigrams2);
    }

    public static BitSet getBigramIndexes(final String phonemePrefix, final int maxBigrams) throws IOException {
        InputStream stream = TrigramSVCalculator.class.getResourceAsStream(phonemePrefix + "bigrams.txt");
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
}
