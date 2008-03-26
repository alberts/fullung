package net.lunglet.sre2008.svm;

import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;

public interface Handle2 {
    FloatVector getData();

    void getData(FloatDenseVector x);

    int getIndex();

    String getName();
}
