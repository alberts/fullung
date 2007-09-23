package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.packed.FloatPackedMatrix;

public interface KernelReader {
    float read(int i, int j);

    FloatPackedMatrix read(int[] indexes);
}
