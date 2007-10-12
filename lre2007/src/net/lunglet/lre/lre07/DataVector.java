package net.lunglet.lre.lre07;

import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.SelectionOperator;

import com.googlecode.array4j.dense.FloatDenseVector;

// TODO get rid of this class

class DataVector implements Comparable<DataVector> {
    private final int id;

    private final DataSet ds;

    private final long index;

    private final String label;

    public DataVector(final int id, final DataSet ds, final long index, final String label) {
        this.id = id;
        this.ds = ds;
        this.index = index;
        this.label = label;
    }

    public DataVector(final int id, final DataSet ds, final long index) {
        this(id, ds, index, null);
    }

    public int getId() {
        return id;
    }

    public boolean hasLabel() {
        return label != null;
    }

    public String getLabel() {
        return label;
    }

    public void read(final FloatDenseVector x) {
        DataSpace fileSpace = ds.getSpace();
        int columns = (int) fileSpace.getDims()[1];
        if (x.length() != columns || x.stride() != 1) {
            throw new IllegalArgumentException();
        }
        DataType dtype = FloatType.IEEE_F32LE;
        SelectionOperator op = SelectionOperator.SET;
        long[] start = {index, 0};
        long[] count = {1, 1};
        long[] blocks = {1, columns};
        fileSpace.selectHyperslab(op, start, null, count, blocks);
        DataSpace memSpace = new DataSpace(blocks);
        ds.read(x.data(), dtype, memSpace, fileSpace);
        memSpace.close();
        fileSpace.close();
    }

    public int length() {
        DataSpace space = ds.getSpace();
        int length = (int) space.getDims()[1];
        space.close();
        return length;
    }

    @Override
    public int compareTo(final DataVector o) {
        int c = ds.compareTo(o.ds);
        if (c != 0) {
            return c;
        }
        return Long.valueOf(index).compareTo(o.index);
    }

    @Override
    public String toString() {
        return id + " -> " + ds.toString() + "#" + index + " = " + label;
    }
}
