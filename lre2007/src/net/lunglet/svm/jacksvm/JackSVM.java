package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.lunglet.hdf.Attribute;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.Point;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.svm.Handle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO clean up datasets contained in DataVectors

// TODO handle case of only 2 unique labels -- train only one model

public final class JackSVM {
    private static long elementOffset(final long m, final long n) {
        if (m > n) {
            throw new IllegalArgumentException();
        }
        // calculate offset for upper triangular entry
        return m + (n + 1L) * n / 2L;
    }

    private final H5File datah5;

    private final H5File kernelh5;

    private final Log log = LogFactory.getLog(JackSVM.class);

    private FloatDenseMatrix models;

    private FloatDenseVector rhos;

    public JackSVM(final H5File datah5, final H5File kernelh5) {
        this.datah5 = datah5;
        this.kernelh5 = kernelh5;
        this.models = null;
        this.rhos = null;
    }

    private List<Handle> createHandles(final Set<String> names) {
        List<Handle> dataList = new ArrayList<Handle>();
        for (String name : names) {
            // TODO code is similar to DataVector stuff
            final DataSet ds = datah5.getRootGroup().openDataSet(name);
            DataSpace space = ds.getSpace();
            final int[] indexes = new int[(int) space.getDim(0)];
            Attribute attr = ds.openAttribute("indexes");
            attr.read(indexes);
            attr.close();
            for (int i = 0; i < space.getDim(0); i++) {
                final int j = i;
//                dataList.add(new Handle() {
//                    @Override
//                    public FloatVector<?> getData() {
//                        DataVector vec = new DataVector(indexes[j], ds, j);
//                        return vec.read();
//                    }
//                });
            }
            space.close();
        }
        return dataList;
    }

    private FloatPackedMatrix readKernel(final List<String> names) {
        SortedSet<Integer> dataIndexes = new TreeSet<Integer>();
        for (String name : names) {
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            int[] indexes = ds.getIntArrayAttribute("indexes");
            for (int i : indexes) {
                dataIndexes.add(i);
            }
            ds.close();
        }
        DataSet kernelds = kernelh5.getRootGroup().openDataSet("/kernel");
        int[] order = kernelds.getIntArrayAttribute("order");
        SortedSet<Integer> kernelIndexes = new TreeSet<Integer>();
        for (int i = 0; i < order.length; i++) {
            if (dataIndexes.contains(order[i])) {
                kernelIndexes.add(i);
            }
        }
        FloatPackedMatrix kernel = FloatPackedMatrix.createSymmetric(kernelIndexes.size(), Storage.DIRECT);
        Integer[] indexes = kernelIndexes.toArray(new Integer[0]);
        // TODO read a row at a time if these lists get too large
        List<Point> srcPoints = new ArrayList<Point>();
        List<Point> destPoints = new ArrayList<Point>();
        for (int i = 0; i < indexes.length; i++) {
            for (int j = i; j < indexes.length; j++) {
                srcPoints.add(new Point(elementOffset(indexes[i], indexes[j])));
                destPoints.add(new Point(elementOffset(i, j)));
            }
        }
        DataSpace memSpace = kernelds.getSpace();
        memSpace.selectElements(SelectionOperator.SET, destPoints.toArray(new Point[0]));
        DataSpace fileSpace = kernelds.getSpace();
        fileSpace.selectElements(SelectionOperator.SET, srcPoints.toArray(new Point[0]));
        kernelds.read(kernel.data(), FloatType.IEEE_F32LE, memSpace, fileSpace);
        memSpace.close();
        fileSpace.close();
        kernelds.close();
        return kernel;
    }

    private List<String> readLabels(final List<String> names) {
        List<String> labels = new ArrayList<String>(names.size());
        for (String name : names) {
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            String label = ds.getStringAttribute("label");
            DataSpace space = ds.getSpace();
            for (long i = 0; i < space.getDim(0); i++) {
                labels.add(label);
            }
            space.close();
            ds.close();
        }
        return labels;
    }

    // TODO return a map of id -> score
    public FloatDenseVector score(final Set<String> names) {
        if (models == null) {
            throw new IllegalStateException();
        }

        System.out.println(models);
        System.out.println(rhos);

        List<Handle> dataList = createHandles(names);
        return null;
    }

    public void train(final Set<String> names) {
//        List<String> namesList = new ArrayList<String>(names);
//        Collections.sort(namesList);
//        // TODO if kernel gets too big, introduce a PrecomputedKernel that reads
//        // Points from the kernel H5 file as required
//        FloatPackedMatrix kernel = readKernel(namesList);
//        List<String> labels = readLabels(namesList);
//        Set<String> uniqueLabels = new HashSet<String>(labels);
//        int modelCount = uniqueLabels.size();
//        log.info(String.format("Will train %d one vs the rest models", modelCount));
//        int modelIndex = 0;
//        for (String label : uniqueLabels) {
//            log.info(String.format("Training model with %s as target label", label));
//            int[] intLabels = new int[labels.size()];
//            for (int i = 0; i < intLabels.length; i++) {
//                intLabels[i] = label.equals(labels.get(i)) ? 1 : -1;
//            }
//            List<Handle> dataList = createHandles(names);
//            Handle[] data = dataList.toArray(new Handle[0]);
//            SimpleSvm svm = new SimpleSvm(data, kernel, intLabels);
//            svm.train(1.0);
//            svm.compact();
//            FloatVector<?> sv = svm.getSupportVector();
//            double rho = svm.getRho();
//            if (models == null) {
//                models = new FloatDenseMatrix(sv.length(), modelCount, Orientation.COLUMN, Storage.DIRECT);
//                rhos = new FloatDenseVector(modelCount);
//            }
//            models.setColumn(modelIndex, sv);
//            rhos.set(modelIndex, (float) rho);
//            modelIndex++;
//        }
    }
}
