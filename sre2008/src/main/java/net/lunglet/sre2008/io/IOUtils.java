package net.lunglet.sre2008.io;

import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IOUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public static DiagCovGMM readDiagCovGMM(final String filename) {
        return null;
    }

    public static void writeGMM(final String filename, final DiagCovGMM gmm) {
        LOGGER.info("Writing GMM to {}", filename);
        H5File h5file = new H5File(filename, H5File.H5F_ACC_TRUNC);
        HDFWriter writer = new HDFWriter(h5file);
        try {
            Group root = h5file.getRootGroup();
            Group means = root.createGroup("/means");
            means.close();
            Group variances = root.createGroup("/variances");
            variances.close();
            writer.write("/weights", DenseFactory.directCopy(gmm.getWeights()));
            for (int i = 0; i < gmm.getMixtureCount(); i++) {
                writer.write("/means/" + i, DenseFactory.directCopy(gmm.getMean(i)));
                writer.write("/variances/" + i, DenseFactory.directCopy(gmm.getVariance(i)));
            }
        } finally {
            writer.close();
        }
    }

    private IOUtils() {
    }
}
