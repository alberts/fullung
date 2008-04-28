package net.lunglet.sre2008.io;

import com.dvsoft.sv.toolbox.matrix.JMatrix;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IOUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public static DiagCovGMM readDiagCovGMM(final File file) {
        return readDiagCovGMM(file.getPath());
    }

    public static DiagCovGMM readDiagCovGMM(final String filename) {
        H5File h5file = new H5File(filename);
        try {
            HDFReader reader = new HDFReader(h5file);
            DataSet dataset = h5file.getRootGroup().openDataSet("/means/0");
            DataSpace space = dataset.getSpace();
            int dimension = space.getIntDims()[0];
            space.close();
            dataset.close();
            dataset = h5file.getRootGroup().openDataSet("/weights");
            space = dataset.getSpace();
            int mixtures = space.getIntDims()[0];
            space.close();
            dataset.close();
            FloatDenseVector weights = DenseFactory.floatRowDirect(mixtures);
            reader.read("/weights", weights);
            FloatDenseVector temp = DenseFactory.floatRowDirect(dimension);
            List<FloatVector> means = new ArrayList<FloatVector>();
            List<FloatVector> variances = new ArrayList<FloatVector>();
            for (int i = 0; i < mixtures; i++) {
                reader.read("/means/" + i, temp);
                means.add(DenseFactory.copyOf(temp));
                reader.read("/variances/" + i, temp);
                variances.add(DenseFactory.copyOf(temp));
            }
            return new DiagCovGMM(weights, means, variances);
        } finally {
            h5file.close();
        }
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
            writer.write("/weights", DenseFactory.directCopyOf(gmm.getWeights()));
            for (int i = 0; i < gmm.getMixtureCount(); i++) {
                writer.write("/means/" + i, DenseFactory.directCopyOf(gmm.getMean(i)));
                writer.write("/variances/" + i, DenseFactory.directCopyOf(gmm.getVariance(i)));
            }
        } finally {
            writer.close();
        }
    }

    public static void writeHLDA(final String filename, final double[] gamma, final JMatrix globalCov,
            final List<JMatrix> classCovs) {
        H5File h5file = new H5File(filename, H5File.H5F_ACC_TRUNC);
        HDFWriter writer = new HDFWriter(h5file);
        try {
            FloatDenseVector g = DenseFactory.floatVector(gamma);
            writer.write("/counts", g);
            writer.write("/globalcov", DenseFactory.floatMatrix(globalCov.toFloatArray(), Order.ROW, Storage.DIRECT));
            int i = 0;
            h5file.getRootGroup().createGroup("classcov").close();
            for (JMatrix classCov : classCovs) {
                String name = "/classcov/" + i;
                writer.write(name, DenseFactory.floatMatrix(classCov.toFloatArray(), Order.ROW, Storage.DIRECT));
                i++;
            }
        } finally {
            writer.close();
        }
    }

    private IOUtils() {
    }
}
