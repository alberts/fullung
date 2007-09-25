package net.lunglet.svm.jacksvm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseVector;

public final class SvmTrainJob implements GridJob {
    private static final long serialVersionUID = 1L;

    private final String modelName;

    private final ArrayList<String> names;

    public SvmTrainJob(final String modelName, final List<String> names) {
        this.modelName = modelName;
        this.names = new ArrayList<String>(names);
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    private static final List<Handle2> readData(final List<String> names, final H5File datah5) {
        List<Handle2> handles = new ArrayList<Handle2>();
        for (final String name : names) {
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            int[] indexes = ds.getIntArrayAttribute("indexes");
            String label = ds.getStringAttribute("label");
            ds.close();
            for (int i = 0; i < indexes.length; i++) {
                final int j = i;
                final int index = indexes[i];
                handles.add(new AbstractHandle2(name, index, label) {
                    @Override
                    public FloatVector<?> getData() {
                        DataSet dataset = datah5.getRootGroup().openDataSet(name);
                        DataSpace fileSpace = dataset.getSpace();
                        int len = (int) fileSpace.getDim(1);
                        DataSpace memSpace = new DataSpace(len);
                        FloatDenseVector data = new FloatDenseVector(len, Orientation.COLUMN, Storage.DIRECT);
                        long[] start = {j, 0};
                        long[] count = {1, 1};
                        long[] block = {1, fileSpace.getDim(1)};
                        fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
                        dataset.read(data.data(), FloatType.IEEE_F32LE, memSpace, fileSpace);
                        fileSpace.close();
                        memSpace.close();
                        dataset.close();
                        return data;
                    }
                });
            }
        }
        return handles;
    }

    @Override
    public Serializable execute() throws GridException {
        H5File kernelh5 = null;
        final H5KernelReader2 kernelReader;
        try {
            System.out.println("reading kernel");
            kernelh5 = new H5File("G:/ngrams_kernel.h5", H5File.H5F_ACC_RDONLY);
            kernelReader = new H5KernelReader2(kernelh5);
            System.out.println("read kernel");
        } finally {
            if (kernelh5 != null) {
                kernelh5.close();
            }
        }
        H5File datah5 = new H5File("G:/ngrams.h5", H5File.H5F_ACC_RDONLY);
        try {
            final List<Handle2> trainData = readData(names, datah5);
            JackSVM2 svm = new JackSVM2(kernelReader);
            svm.train(trainData);
            svm.compact();
            return new Object[]{modelName, svm};
        } finally {
            datah5.close();
        }
    }
}
