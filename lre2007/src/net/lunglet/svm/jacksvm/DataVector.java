package net.lunglet.svm.jacksvm;

import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.SelectionOperator;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseVector;

// TODO let datavector open dataset when it reads

class DataVector implements Comparable<DataVector> {
    private final DataSet dataset;

    private final int index;

    private final String label;

    private final long row;

    public DataVector(final int index, final DataSet dataset, final long row) {
        this(index, dataset, row, null);
    }

    public DataVector(final int index, final DataSet dataset, final long row, final String label) {
        this.index = index;
        this.dataset = dataset;
        this.row = row;
        this.label = label;
    }

    @Override
    public int compareTo(final DataVector o) {
        int c = dataset.compareTo(o.dataset);
        if (c != 0) {
            return c;
        }
        return Long.valueOf(row).compareTo(o.row);
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return dataset.getName();
    }

    public long getRow() {
        return row;
    }

    public boolean hasLabel() {
        return label != null;
    }

    public int length() {
        DataSpace space = dataset.getSpace();
        int length = (int) space.getDims()[1];
        space.close();
        return length;
    }

    public FloatDenseVector read() {
        FloatDenseVector x = new FloatDenseVector(length(), Orientation.DEFAULT_FOR_VECTOR, Storage.DIRECT);
        read(x);
        return x;
    }

    public void read(final FloatDenseVector x) {
        DataSpace fileSpace = dataset.getSpace();
        int columns = (int) fileSpace.getDims()[1];
        if (x.length() != columns || x.stride() != 1) {
            throw new IllegalArgumentException();
        }
        DataType dtype = FloatType.IEEE_F32LE;
        SelectionOperator op = SelectionOperator.SET;
        long[] start = {row, 0};
        long[] count = {1, 1};
        long[] blocks = {1, columns};
        fileSpace.selectHyperslab(op, start, null, count, blocks);
        DataSpace memSpace = new DataSpace(blocks);
        dataset.read(x.data(), dtype, memSpace, fileSpace);
        memSpace.close();
        fileSpace.close();
    }

    @Override
    public String toString() {
        return index + " -> " + dataset.toString() + "#" + row + " = " + label;
    }
}
