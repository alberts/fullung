package net.lunglet.svm.jacksvm;

import static org.junit.Assert.assertEquals;
import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.io.HDFReader;
import com.googlecode.array4j.io.HDFWriter;
import com.googlecode.array4j.math.FloatMatrixMath;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import net.lunglet.svm.jacksvm.Handle2.Score;
import org.junit.Test;

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
//        int datasets = 30;
//        int classes = 3;
        int datasets = 10;
        int classes = 2;
        Random rng = new Random(1234);
        int dataColumns = 10;
        List<FloatDenseMatrix> dataList = new ArrayList<FloatDenseMatrix>();
        int dataRows = 0;
        int dataIndex = 0;
        HDFWriter dataWriter = new HDFWriter(datah5);
        List<Handle2> handleList = new ArrayList<Handle2>();
        for (int i = 0; i < datasets; i++) {
//            int rows = 1 + rng.nextInt(6);
            int rows = 1;
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

    private Map<String, List<Score>> createScoresMap(final List<Handle2> data) {
        Map<String, List<Score>> scoresMap = new HashMap<String, List<Score>>();
        for (Handle2 handle : data) {
            scoresMap.put(handle.getName(), handle.getScores());
        }
        return scoresMap;
    }

    @Test
    public void test() throws IOException, ClassNotFoundException {
        H5File datah5 = createMemoryH5File();
        H5File kernelh5 = createMemoryH5File();
        List<Handle2> data = createData(datah5);

        final Map<Integer, Handle2> indexDataMap = new HashMap<Integer, Handle2>();
        for (Handle2 handle : data) {
            indexDataMap.put(handle.getIndex(), handle);
        }

        Set<String> names = getNames(datah5);
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5);
//        kernelComputer.compute(names);
        checkKernel(datah5, kernelh5);

        // create reference scores
        JackSVM2 svm = new JackSVM2(new H5KernelReader(kernelh5));
        svm.train(data);
        CompactJackSVM2Builder svmBuilder = svm.getCompactBuilder();
        for (Handle2 x : data) {
            svmBuilder.present(x.getData(), x.getIndex());
        }
        JackSVM2 compactSvm = svmBuilder.build();
        compactSvm.score(data);
        Map<String, List<Score>> expectedScoresMap = createScoresMap(data);

        // serialize svm before and after compaction
        JackSVM2 svm1 = new JackSVM2(new H5KernelReader(kernelh5));
        svm1.train(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(svm1);
        oos.reset();
        svmBuilder = svm1.getCompactBuilder();
        for (Handle2 x : data) {
            svmBuilder.present(x.getData(), x.getIndex());
        }
        oos.writeObject(svmBuilder.build());
        oos.reset();
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        JackSVM2 svm2 = (JackSVM2) ois.readObject();
        JackSVM2 svm3 = (JackSVM2) ois.readObject();
        ois.close();
        svmBuilder = svm2.getCompactBuilder();
        for (Handle2 x : data) {
            svmBuilder.present(x.getData(), x.getIndex());
        }
        JackSVM2 compactSvm2 = svmBuilder.build();
        compactSvm2.score(data);
        Map<String, List<Score>> scoresMap2 = createScoresMap(data);
        // TODO do an almostEquals comparison on the scores
//        assertEquals(expectedScoresMap, scoresMap2);
        System.out.println(expectedScoresMap);
        System.out.println(scoresMap2);

        svm3.score(data);
        Map<String, List<Score>> scoresMap3 = createScoresMap(data);
        assertEquals(expectedScoresMap, scoresMap3);

        kernelh5.close();
        datah5.close();
    }
}
