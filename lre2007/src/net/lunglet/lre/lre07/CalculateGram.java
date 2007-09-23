package net.lunglet.lre.lre07;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lunglet.hdf.Attribute;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSetCreatePropListBuilder;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.IntType;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.hdf.DataSetCreatePropListBuilder.FillTime;
import net.lunglet.io.FileUtils;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.util.BufferUtils;

public final class CalculateGram {
    private static final int GEMM_BUFFER_SIZE = 2400;

    public static Set<String> readFrontendIds() throws IOException {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("frontend_") && name.endsWith(".txt");
            }
        };
        File[] files = FileUtils.listFiles("C:/home/albert/LRE2007/keysetc/albert/output", filter);
        Set<String> ids = new HashSet<String>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("\\s+");
                String[] idparts = parts[1].split(",", 2);
                String id = "/" + idparts[0] + "/" + idparts[1];
                ids.add(id);
                line = reader.readLine();
            }
        }
        return ids;
    }

    public static void main(final String[] args) throws IOException {
        System.out.println(System.currentTimeMillis());
        System.out.println("reading frontend ids");
        Set<String> frontendIds = readFrontendIds();
        System.out.println("got " + frontendIds.size() + " frontend ids");
        H5File ngramsh5 = new H5File("F:/ngrams.h5", H5File.H5F_ACC_RDONLY);
        // TODO should probably close proplist
        Group root = ngramsh5.getRootGroup();
        List<DataVector> vecs = new ArrayList<DataVector>();
        System.out.println("finding supervectors");
        for (Group group : root.getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                String name = ds.getName();
                if (!frontendIds.contains(name)) {
                    continue;
                }
                frontendIds.remove(name);
                DataSpace space = ds.getSpace();
                int[] id = new int[(int) space.getDim(0)];
                Attribute attr = ds.openAttribute("id");
                attr.read(id);
                attr.close();
                for (int i = 0; i < space.getDim(0); i++) {
                    vecs.add(new DataVector(id[i], ds, i));
                }
                space.close();
            }
            group.close();
        }
        if (frontendIds.size() != 0) {
            throw new AssertionError();
        }
        System.out.println("found " + vecs.size() + " supervectors");
        Collections.sort(vecs);
        System.out.println("calculating gram");
        calculateGram(vecs);
        System.out.println("gram is done");
        ngramsh5.close();
        System.out.println(System.currentTimeMillis());
    }

    private static void calculateGram(final List<DataVector> vecs) {
        if (vecs.size() == 0) {
            throw new IllegalArgumentException();
        }

        // create output file
        H5File gramh5 = new H5File("F:/ngrams_gram.h5");

        // create dataset in output file
        DataType dtype = FloatType.IEEE_F32LE;
        long gramdim = vecs.size() * (vecs.size() + 1L) / 2L;
        System.out.println(gramdim);
        DataSpace gramspace = new DataSpace(gramdim);
        DataSetCreatePropListBuilder builder = new DataSetCreatePropListBuilder();
        builder.setFillTime(FillTime.NEVER);
        DataSet gramds = gramh5.getRootGroup().createDataSet("gram", dtype, gramspace, builder.build());
        gramspace.close();

        int[] order = new int[vecs.size()];

        // calculate parts of gram matrix
        int bufrows = vecs.get(0).length();
        System.out.println(bufrows);
        Orientation orient = Orientation.COLUMN;
        Storage storage = Storage.DIRECT;
        FloatDenseMatrix a = new FloatDenseMatrix(bufrows, GEMM_BUFFER_SIZE, orient, storage);
        FloatDenseMatrix b = new FloatDenseMatrix(bufrows, GEMM_BUFFER_SIZE, orient, storage);
        FloatBuffer cbuf = BufferUtils.createFloatBuffer(GEMM_BUFFER_SIZE * GEMM_BUFFER_SIZE, storage);
        for (int i = 0; i < vecs.size(); i += a.columns()) {
            int j = Math.min(i + a.columns(), vecs.size());
            List<DataVector> xvecs = vecs.subList(i, j);
            for (int m = 0; m < xvecs.size(); m++) {
                xvecs.get(m).read(a.column(m));
                order[i + m] = xvecs.get(m).getId();
            }
            System.out.println("read x[" + i + ", " + j + "] done");
            FloatDenseMatrix x = FloatDenseUtils.subMatrixColumns(a, 0, xvecs.size());
            for (int k = i; k < vecs.size(); k += b.columns()) {
                System.out.println("read y");
                int l = Math.min(k + b.columns(), vecs.size());
                final FloatDenseMatrix y;
                if (k == i) {
                    y = x;
                } else {
                    List<DataVector> yvecs = vecs.subList(k, l);
                    for (int m = 0; m < yvecs.size(); m++) {
                        yvecs.get(m).read(b.column(m));
                    }
                    y = FloatDenseUtils.subMatrixColumns(b, 0, yvecs.size());
                }
                System.out.println("read y[" + k + ", " + l + "] done");
                System.out.println("gemm");
                // TODO just take a view on an existing grampart here
                // TODO will require proper support for BLAS's leading dimension concept
//                FloatDenseMatrix grampart = new FloatDenseMatrix(x.columns(), y.columns(), orient, storage);
                FloatDenseMatrix grampart = new FloatDenseMatrix(cbuf, x.columns(), y.columns(), 0, 1, orient);
                // TODO might want to do a syrk here when x==y
                FloatDenseBLAS.DEFAULT.gemm(1.0f, x.transpose(), y, 0.0f, grampart);
                System.out.println("gemm done");
                System.out.println("writing");
                for (int m = 0; m < grampart.columns(); m++) {
                    FloatDenseVector gramcol = grampart.column(m);
                    if (gramcol.stride() != 1) {
                        throw new AssertionError();
                    }
                    int gramcolidx = m + k;
                    DataSpace memSpace = new DataSpace(Math.min(gramcolidx + 1, grampart.rows()));
                    DataSpace fileSpace = gramds.getSpace();
                    long[] start = new long[]{elementOffset(i, gramcolidx)};
                    long[] count = new long[]{1L};
                    long[] block = new long[]{memSpace.getDim(0)};
                    fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
                    gramds.write(gramcol.data(), dtype, memSpace, fileSpace);
                    fileSpace.close();
                    memSpace.close();
                }
                System.out.println("writing done");
            }
        }

        DataSpace space = new DataSpace(order.length);
        Attribute attr = gramds.createAttribute("order", IntType.STD_I32LE, space);
        attr.write(order);
        attr.close();
        space.close();

        gramds.close();
        gramh5.close();
    }

    private static long elementOffset(final long m, final long n) {
        if (m > n) {
            throw new IllegalArgumentException();
        }
        // calculate offset for upper triangular entry
        return m + (n + 1L) * n / 2L;
    }
}
