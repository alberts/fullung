package net.lunglet.sre2008.util;

import java.io.File;
import java.io.FilenameFilter;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.H5File;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;

public final class MergeTNorm {
    public static void main(final String[] args) {
        FilenameFilter filter = new FilenameSuffixFilter(".gmmsvm", true);
        String path = "Z:\\data\\nap512v2\\tnorm";
        File[] tnormFiles = FileUtils.listFiles(path, filter, true);
        HDFReader reader = new HDFReader(16 * 1024 * 1024);
        HDFWriter writer = new HDFWriter("tnorm.h5");
        int i = 0;
        FloatDenseMatrix tnormSvm = DenseFactory.floatRowDirect(512 * 38 + 1);
        for (File file : tnormFiles) {
            System.out.println(file);
            H5File h5file = new H5File(file);
            reader.read(h5file, "/svm", tnormSvm);
            writer.write("/tnorm" + i, tnormSvm);
            h5file.close();
            i++;
        }
        writer.close();
        reader.close();
    }
}
