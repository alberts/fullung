package net.lunglet.svm.jacksvm;

import static org.junit.Assert.assertTrue;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FileAccessPropList;
import net.lunglet.hdf.FileAccessPropListBuilder;
import net.lunglet.hdf.FileCreatePropList;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

public final class LinearKernelPrecomputerTest {
    private static H5File createMemoryH5File() {
        FileCreatePropList fcpl = FileCreatePropList.DEFAULT;
        FileAccessPropList fapl = new FileAccessPropListBuilder().setCore(1024, false).build();
        H5File h5file = new H5File(UUID.randomUUID().toString(), fcpl, fapl);
        fapl.close();
        return h5file;
    }

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

    private void checkKernel(final H5File datah5, final H5File kernelh5) {
        Set<String> names = getNames(datah5);

        // TODO get rid of DataVector
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5);
        Map<Integer, DataVector> vecs = kernelComputer.readDataVectors(names);

        H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        DataSet kernelds = kernelh5.getRootGroup().openDataSet("/kernel");
        int[] order = kernelds.getIntArrayAttribute("order");
        Set<Integer> validIndexes = new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(order)));
        kernelds.close();
        for (int i = 0; i < vecs.size(); i++) {
            DataVector veci = vecs.get(i);
            if (!validIndexes.contains(veci.getIndex())) {
                continue;
            }
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
                assertTrue(delta < 1e-3);
            }
        }
    }

    private void createData(final H5File datah5, final int rows) {
        Group group = datah5.getRootGroup().createGroup("foo");
        DataType dtype = FloatType.IEEE_F32LE;
        int columns = 2;
        DataSet dataset = group.createDataSet("bar", dtype, rows, columns);
        float[] values = new float[rows * columns];
        for (int i = 0; i < values.length; i++) {
            values[i] = i + 1.0f;
        }
        dataset.write(values);
        int[] indexes = new int[rows];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        dataset.createAttribute("indexes", indexes);
        dataset.close();
        group.close();
    }

    @Test
    public void test() {
        for (int bufferSize = 1; bufferSize <= 15; bufferSize++) {
            for (int rows = 1; rows < 20; rows++) {
                H5File datah5 = createMemoryH5File();
                H5File kernelh5 = createMemoryH5File();
                createData(datah5, rows);
                LinearKernelPrecomputer precomputer = new LinearKernelPrecomputer(datah5, kernelh5, bufferSize);
                Set<String> names = new HashSet<String>();
                Collections.addAll(names, "/foo/bar");
                precomputer.compute(names);
                checkKernel(datah5, kernelh5);
                kernelh5.close();
                datah5.close();
            }
        }
    }
}
