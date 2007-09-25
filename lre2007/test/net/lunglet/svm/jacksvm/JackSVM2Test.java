package net.lunglet.svm.jacksvm;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.FileAccessPropList;
import net.lunglet.hdf.FileAccessPropListBuilder;
import net.lunglet.hdf.FileCreatePropList;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;

import org.junit.Test;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.io.HDFReader;
import com.googlecode.array4j.io.HDFWriter;
import com.googlecode.array4j.math.FloatMatrixMath;
import com.googlecode.array4j.packed.FloatPackedMatrix;

public final class JackSVM2Test {
    public static H5File createMemoryH5File() {
        FileCreatePropList fcpl = FileCreatePropList.DEFAULT;
        FileAccessPropList fapl = new FileAccessPropListBuilder().setCore(16384 * 1024, false).build();
        H5File h5file = new H5File(UUID.randomUUID().toString(), fcpl, fapl);
        fapl.close();
        return h5file;
    }

    private void checkKernel(final H5File datah5, final H5File kernelh5) {
        Set<String> names = getNames(datah5);
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5);
        Map<Integer, DataVector> vecs = kernelComputer.readDataVectors(names);
        DataSet ds = kernelh5.getRootGroup().openDataSet("/kernel");
        int[] order = ds.getIntArrayAttribute("order");
        HDFReader reader = new HDFReader(kernelh5);
        FloatPackedMatrix kernel = FloatPackedMatrix.createSymmetric(order.length, Storage.DIRECT);
        reader.read("/kernel", kernel);
        for (int i = 0; i < order.length; i++) {
            FloatDenseVector x = vecs.get(order[i]).read();
            for (int j = i; j < order.length; j++) {
                FloatDenseVector y = vecs.get(order[j]).read();
                float k = FloatMatrixMath.dot(x, y);
                assertEquals(k, kernel.get(i, j), 1e-4);
            }
        }
        ds.close();
    }

    private List<Handle2> createData(final H5File datah5) {
        Group dataRoot = datah5.getRootGroup();
        dataRoot.createGroup("/foo").close();
        int datasets = 30;
        int classes = 3;
        Random rng = new Random();
        //Random rng = new Random(1234);
        //Random rng = new Random(-1231429939);
        int dataColumns = 10;
        List<FloatDenseMatrix> dataList = new ArrayList<FloatDenseMatrix>();
        int dataRows = 0;
        int dataIndex = 0;
        HDFWriter dataWriter = new HDFWriter(datah5);
        List<Handle2> handleList = new ArrayList<Handle2>();
        for (int i = 0; i < datasets; i++) {
            int rows = 1 + rng.nextInt(6);
            final FloatDenseMatrix x = new FloatDenseMatrix(rows, dataColumns);
            int labelIndex = rng.nextInt(classes);
            FloatMatrixUtils.fillGaussian(x, labelIndex, 0.01, rng);
            final String name = "/foo/data" + i;
            dataWriter.write(name, x);
            DataSet ds = dataRoot.openDataSet(name);
            final String label = "label" + labelIndex;
            ds.createAttribute("label", label);
            final int[] indexes = new int[rows];
            for (int j = 0; j < indexes.length; j++) {
                indexes[j] = dataIndex++;
                // XXX elaborate way to copy vector
                final FloatDenseVector colVec = new FloatDenseMatrix(x.row(j)).transpose().column(0);
                final int index = indexes[j];
                handleList.add(new AbstractHandle2(name, index, label) {
                    @Override
                    public FloatDenseVector getData() {
                        return colVec;
                    }
                });
            }
            ds.createAttribute("indexes", indexes);
            ds.close();
            dataList.add(x);
            dataRows += rows;
        }
        FloatDenseMatrix data = new FloatDenseMatrix(dataRows, dataColumns);
        for (int i = 0, j = 0; i < dataList.size(); i++) {
            for (int k = 0; k < dataList.get(i).rows(); k++) {
                data.setRow(j + k, dataList.get(i).row(k));
            }
            j += dataList.get(i).rows();
        }
        return handleList;
    }

    // TODO generate nicer data here

    private Set<String> getNames(final H5File datah5) {
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

    @Test
    public void test() throws IOException, ClassNotFoundException {
        H5File datah5 = createMemoryH5File();
        H5File kernelh5 = createMemoryH5File();
        List<Handle2> data = createData(datah5);
        Set<String> names = getNames(datah5);
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5);
        kernelComputer.compute(names);
        checkKernel(datah5, kernelh5);
        JackSVM2 svm = new JackSVM2(new H5KernelReader(kernelh5));
        svm.train(data);
        svm.compact();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(svm);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        JackSVM2 svm2 = (JackSVM2) ois.readObject();
        ois.close();
        svm2.score(data);
        for (Handle2 handle : data) {
            System.out.println(String.format("%s %d %s %s", handle.getName(), handle.getIndex(), handle.getLabel(),
                handle.getScores()));
        }
        kernelh5.close();
        datah5.close();
    }
}
