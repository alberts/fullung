package cz.vutbr.fit.speech.phnrec;

import com.googlecode.array4j.MatrixTestSupport;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import java.util.BitSet;
import org.junit.Test;

public final class PhonemeUtilTest {
    @Test
    public void test() {
        int m = 10;
        int n = 100;
        FloatDenseMatrix posteriors = new FloatDenseMatrix(m, n, Orientation.COLUMN, Storage.DIRECT);
        MatrixTestSupport.populateMatrix(posteriors);
        
        BitSet bigramIndexes = new BitSet();
        bigramIndexes.set(0, posteriors.rows() * posteriors.rows());

        FloatDenseVector monograms = PhonemeUtil.calculateMonograms(posteriors);
        System.out.println(monograms);

        FloatDenseVector bigrams = PhonemeUtil.calculateBigrams(posteriors, 1);
        System.out.println(bigrams);

        FloatDenseVector stagbi = PhonemeUtil.calculateBigrams(posteriors, 2);
        System.out.println(stagbi);

        FloatDenseVector trigrams = PhonemeUtil.calculateTrigrams(posteriors, bigramIndexes);
        System.out.println(trigrams);
    }
}
