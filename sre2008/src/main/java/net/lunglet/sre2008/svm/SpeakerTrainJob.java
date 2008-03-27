package net.lunglet.sre2008.svm;

import java.util.Arrays;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.GMMTrainer;
import net.lunglet.sre2008.io.IOUtils;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpeakerTrainJob implements GridJob, Comparable<SpeakerTrainJob> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeakerTrainJob.class);

    private static final long serialVersionUID = 1L;

    private final String datah5;

    private final String name;

    private final String ubmh5;

    public SpeakerTrainJob(final String name, final String ubmh5, final String datah5) {
        this.name = name;
        this.ubmh5 = ubmh5;
        this.datah5 = datah5;
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(final SpeakerTrainJob other) {
        return name.compareTo(other.name);
    }

    @Override
    public SpeakerTrainResult execute() throws GridException {
        LOGGER.info("Training GMM supervector for {}", name);
        FloatDenseMatrix data = readData();
        DiagCovGMM ubm = readUBM();
        return new SpeakerTrainResult(name, GMMTrainer.train(ubm, data));
    }

    private FloatDenseMatrix readData() {
        H5File h5file = new H5File(datah5);
        HDFReader reader = new HDFReader(h5file);
        DataSet dataset = h5file.getRootGroup().openDataSet(name);
        int[] dims = dataset.getIntDims();
        dataset.close();
        FloatDenseMatrix data = DenseFactory.floatRowDirect(dims);
        LOGGER.info("Loaded data from {} {}", name, Arrays.toString(dims));
        reader.read(name, data);
        reader.close();
        return data;
    }

    private DiagCovGMM readUBM() {
        LOGGER.info("Reading UBM from " + ubmh5);
        DiagCovGMM ubm = IOUtils.readDiagCovGMM(ubmh5);
        if (!GMMUtils.isGMMParametersFinite(ubm)) {
            LOGGER.error("UBM contains invalid parameters");
            throw new RuntimeException();
        }
        return ubm;
    }
}
