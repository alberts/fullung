package net.lunglet.svm.jacksvm;

import static org.junit.Assert.assertEquals;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import java.util.Random;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import org.junit.Test;

public final class H5KernelReaderTest {
    @Test
    public void test() {
        H5File kernelh5 = JackSVM2Test.createMemoryH5File();
        Group root = kernelh5.getRootGroup();
        DataSet kernelds = root.createDataSet("/kernel", FloatType.IEEE_F32LE, 3);
        kernelds.write(new float[]{1.0f, 2.0f, 3.0f});
        kernelds.createAttribute("order", new int[]{1, 0});
        kernelds.close();
//        H5KernelReader kernelReader = new H5KernelReader(kernelh5);
        H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        FloatPackedMatrix kernel = null;
        Random rng = new Random(0);
        for (int n = 0; n < 10; n++) {
            int[] indexes = new int[n];
            for (int j = 0; j < indexes.length; j++) {
                indexes[j] = rng.nextInt(2);
            }
            kernel = kernelReader.read(indexes);
            assertEquals(indexes.length, kernel.rows());
            for (int j = 0; j < indexes.length; j++) {
                for (int k = 0; k < indexes.length; k++) {
                    if (indexes[j] == indexes[k]) {
                        if (indexes[j] == 0) {
                            assertEquals(3.0f, kernel.get(j, k), 0);
                        } else {
                            assertEquals(1.0f, kernel.get(j, k), 0);
                        }
                    } else {
                        assertEquals(2.0f, kernel.get(j, k), 0);
                    }
                    assertEquals(kernel.get(j, k), kernelReader.read(indexes[j], indexes[k]), 0);
                }
            }
        }
        kernelh5.close();
    }
}
