package net.lunglet.sre2008;

import java.io.Serializable;
import java.util.Arrays;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.svm.Handle;
import net.lunglet.util.ArrayMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HDFHandle implements Handle, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(HDFHandle.class);

    private static final HDFReader READER = new HDFReader(16 * 1024 * 1024);

    private static final long serialVersionUID = 1L;

    private transient FloatDenseVector data;

    private final String h5;

    private final int index;

    private final int label;

    private final String name;

    public HDFHandle(final String h5, final String name, final int index, final int label) {
        this.h5 = h5;
        this.name = name;
        this.index = index;
        this.label = label;
        this.data = null;
    }

    @Override
    public synchronized FloatVector getData() {
        if (data != null) {
            return data;
        }
        H5File h5file = new H5File(h5);
        DataSet dataset = h5file.getRootGroup().openDataSet(name);
        int[] dims = dataset.getIntDims();
        dataset.close();
        if (dims.length > 2 || (dims.length == 2 && dims[0] > 1 && dims[1] > 1)) {
            throw new RuntimeException();
        }
        data = DenseFactory.floatRow(ArrayMath.max(dims));
        LOGGER.debug("Loading background data from {} {}", name, Arrays.toString(dims));
        // access to this reader is synchronized, as it should be
        READER.read(h5file, name, data);
        h5file.close();
        return data;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getLabel() {
        return label;
    }
}
