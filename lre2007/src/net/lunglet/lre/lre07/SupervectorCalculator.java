package net.lunglet.lre.lre07;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;

public interface SupervectorCalculator {
    FloatDenseVector apply(FloatDenseMatrix data);
}
