package net.lunglet.sre2008.util;

import java.io.File;
import java.io.FilenameFilter;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;

public final class MergeEval {
    public static void main(final String[] args) {
        FilenameFilter filter = new FilenameSuffixFilter(".gmmsvm", true);
        String path = "Z:\\data\\nap512v2\\sre05";
        File[] files = FileUtils.listFiles(path, filter, true);
        HDFReader reader = new HDFReader(16 * 1024 * 1024);
        HDFWriter writer = new HDFWriter("eval05.h5");
        int i = 0;
        FloatDenseMatrix gmm = DenseFactory.floatRowDirect(512 * 38);
        FloatDenseMatrix svm = DenseFactory.floatRowDirect(512 * 38 + 1);
        for (File file : files) {
            System.out.println(file);
            H5File h5file = new H5File(file);
            String[] parts = file.getName().split("\\.");
            String hdfName = String.format("/%s/%s", parts[0], parts[4]);
            Group root = writer.getH5File().getRootGroup();
            if (!root.existsGroup("/" + parts[0])) {
                root.createGroup("/" + parts[0]);
            }
            if (!root.existsGroup(hdfName)) {
                root.createGroup(hdfName);
            }
            reader.read(h5file, "/gmm", gmm);
            reader.read(h5file, "/svm", svm);
            writer.write(hdfName + "/gmm", gmm);
            writer.write(hdfName + "/svm", svm);
            h5file.close();
            i++;
        }
        writer.close();
        reader.close();
    }
}
