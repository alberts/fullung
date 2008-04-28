package net.lunglet.sre2008.svm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.MatrixTestSupport;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import org.junit.Test;

public final class SpeakerKernelMatrixTest {
    @Test
    public void test() {
        FloatMatrix background = DenseFactory.floatMatrix(3, 3);
        MatrixTestSupport.populateMatrix(background);
        FloatMatrix speaker = DenseFactory.floatMatrix(5, 2);
        MatrixTestSupport.populateMatrix(speaker);        
        FloatMatrix kernel = new SpeakerKernelMatrix(background, speaker);
        assertEquals(5, kernel.rows());
        assertEquals(5, kernel.columns());
        assertTrue(kernel.isSquare());
        for (int i = 0; i < kernel.rows(); i++) {
            for (int j = 0; j < kernel.columns(); j++) {
                assertEquals(kernel.get(i, j), kernel.get(j, i), 0);
            }
        }
        System.out.println(background);
        System.out.println(speaker);
        System.out.println(kernel);
    }
}
