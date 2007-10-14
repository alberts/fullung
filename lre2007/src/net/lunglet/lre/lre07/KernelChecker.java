package net.lunglet.lre.lre07;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.lunglet.hdf.Attribute;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.svm.jacksvm.H5KernelReader2;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class KernelChecker {
    private static class DataVector implements Comparable<DataVector> {
        private final DataSet dataset;

        private final int index;

        private final long row;

        public DataVector(final int index, final DataSet dataset, final long row) {
            this.index = index;
            this.dataset = dataset;
            this.row = row;
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

        public String getName() {
            return dataset.getName();
        }

        public long getRow() {
            return row;
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
            return index + " -> " + dataset.toString() + "#" + row;
        }
    }

    public static Map<Integer, DataVector> readDataVectors(final H5File datah5, final Set<String> names) {
        Set<String> namesCopy = new HashSet<String>(names);
        Group root = datah5.getRootGroup();
        Map<Integer, DataVector> vecs = new HashMap<Integer, DataVector>();
        for (Group group : root.getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                String name = ds.getName();
                if (!names.contains(name)) {
                    continue;
                }
                namesCopy.remove(name);
                DataSpace space = ds.getSpace();
                int[] indexes = new int[(int) space.getDim(0)];
                Attribute attr = ds.openAttribute("indexes");
                attr.read(indexes);
                attr.close();
                for (int i = 0; i < space.getDim(0); i++) {
                    vecs.put(indexes[i], new DataVector(indexes[i], ds, i));
                }
                space.close();
            }
            group.close();
        }
        if (namesCopy.size() != 0) {
            throw new AssertionError();
        }
        return vecs;
    }

    private static final Log LOG = LogFactory.getLog(KernelChecker.class);

    private static Set<String> getNames(final H5File datah5) {
        Set<String> names = new HashSet<String>();
        for (Group group : datah5.getRootGroup().getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                names.add(ds.getName());
                ds.close();
            }
            group.close();
        }
        return names;
    }

    public static void main(final String[] args) {
        H5File datah5 = new H5File("G:/czngrams.h5", H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File("G:/czkernel.h5", H5File.H5F_ACC_RDONLY);
        LOG.info("getting names of datasets");
        Set<String> names = getNames(datah5);

        // TODO get rid of DataVector
        LOG.info("opening data vectors");
        Map<Integer, DataVector> vecs = readDataVectors(datah5, names);

        LOG.info("reading kernel");
        H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        LOG.info("read kernel");
        DataSet kernelds = kernelh5.getRootGroup().openDataSet("/kernel");
        int[] order = kernelds.getIntArrayAttribute("order");
        Set<Integer> validIndexes = new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(order)));
        kernelds.close();
        int errorCount = 0;
        for (int i = 0; i < vecs.size(); i++) {
            DataVector veci = vecs.get(i);
            if (!validIndexes.contains(veci.getIndex())) {
                continue;
            }
            LOG.info(String.format("checking %s[%d] (%d)", veci.getName(), veci.getRow(), veci.getIndex()));
            FloatDenseVector x = veci.read();
            for (int j = i; j < vecs.size(); j++) {
                DataVector vecj = vecs.get(j);
                if (!validIndexes.contains(vecj.getIndex())) {
                    continue;
                }
                FloatDenseVector y = vecj.read();
                float kread = kernelReader.read(veci.getIndex(), vecj.getIndex());
                float k = FloatMatrixMath.dot(x, y);
                float delta = Math.abs((kread - k) / k);
                if (delta > 1e-3) {
                    LOG.error(String.format("invalid value for K(%d, %d): expected=%.15e, actual=%.15e, delta = %.15e",
                        veci.getIndex(), vecj.getIndex(), k, kread, delta));
                    errorCount++;
                    if (errorCount > 20) {
                        LOG.fatal("too many errors, exiting");
                        return;
                    }
                }
            }
        }
        kernelh5.close();
        datah5.close();
    }
}
