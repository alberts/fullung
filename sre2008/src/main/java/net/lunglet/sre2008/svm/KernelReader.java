package net.lunglet.sre2008.svm;

import net.lunglet.array4j.matrix.packed.FloatPackedMatrix;

public interface KernelReader {
    float read(int i, int j);

    FloatPackedMatrix read(int[] indexes);
}
