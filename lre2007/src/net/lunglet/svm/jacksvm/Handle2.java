package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.dense.FloatDenseVector;

// TODO another thing user might want to get via the handle is the
// dimensions of the data it points to, without loading the data
// (or maybe just loading a header)

// TODO look at readResolve/writeReplace

// TODO maybe use UUIDs to identify handles

public interface Handle2 extends Comparable<Handle2> {
    FloatVector<?> getData();

    void getData(FloatDenseVector x);

    int getIndex();

    String getLabel();

    String getName();

    int getDuration();
}
