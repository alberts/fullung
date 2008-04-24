package net.lunglet.sre2008.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.TrainGMM3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MergeFeatures {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MergeFeatures.class);

    public static void main(final String[] args) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        if (args.length != 2 && args.length != 3) {
            LOGGER.error("Usage: MergeFeatures FILELIST OUTPUTH5 [BASENAME]");
            System.exit(1);
        }
        List<String> names = TrainGMM3.readFilelist(args[0]);
        File outputFile = new File(args[1]);
        if (outputFile.exists()) {
            LOGGER.error("Output file {} already exists", outputFile);
            System.exit(2);
        }
        String basename = args.length == 3 ? args[2] : null;
        HDFReader reader = new HDFReader(0);
        HDFWriter writer = new HDFWriter(outputFile);
        int baseCount = 0;
        for (String name : names) {
            H5File h5file = new H5File(name);
            String[] parts = new File(name).getName().split("\\.");
            for (int i = 0; i < 2; i++) {
                String inputName = "/mfcc/" + i;
                if (!h5file.getRootGroup().existsDataSet(inputName)) {
                    continue;
                }
                DataSet dataset = h5file.getRootGroup().openDataSet(inputName);
                int[] dims = dataset.getIntDims();
                FloatDenseMatrix mfcc = DenseFactory.floatRowDirect(dims);
                reader.read(h5file, inputName, mfcc);
                final String outputName;
                if (basename != null) {
                    outputName = "/" + basename + baseCount;
                    baseCount++;
                } else {
                    outputName = "/" + parts[0] + "/" + i;
                    if (!writer.getH5File().getRootGroup().existsGroup("/" + parts[0])) {
                        writer.getH5File().getRootGroup().createGroup("/" + parts[0]);
                    }
                }
                LOGGER.info("{} -> {}", name, outputName);
                writer.write(outputName, mfcc);
            }
            h5file.close();
        }
        writer.close();
        reader.close();
        LOGGER.info("Shutting down");
    }
}
