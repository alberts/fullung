package net.lunglet.sre2008.svm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.lunglet.array4j.Direction;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.util.FloatMatrixUtils;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMMAPStats;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.gridgain.DefaultGrid;
import net.lunglet.gridgain.LocalGrid;
import net.lunglet.gridgain.ResultHandler;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.io.IOUtils;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO rank normalization

public final class TrainBackground {
    public static final class SpeakerTrainResult implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;

        private final float[] sv;

        public SpeakerTrainResult(final String name, final FloatVector sv) {
            this.name = name;
            // XXX using a float array for now because FloatVectors don't
            // serialize properly yet
            this.sv = sv.toArray();
        }
    }

    public static class SpeakerTrainJob implements GridJob, Comparable<SpeakerTrainJob> {
        private static final long serialVersionUID = 1L;

        private final String name;

        public SpeakerTrainJob(final String name) {
            this.name = name;
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpeakerTrainResult execute() throws GridException {
            final DiagCovGMM ubm;
            final FloatDenseMatrix data;
            synchronized (H5File.class) {
                LOGGER.info("Reading UBM from " + UBM_FILE);
                ubm = IOUtils.readDiagCovGMM(UBM_FILE);
                if (!GMMUtils.isGMMParametersFinite(ubm)) {
                    LOGGER.error("UBM contains invalid parameters");
                    throw new RuntimeException();
                }
                H5File h5file = new H5File(DATA_FILE);
                HDFReader reader = new HDFReader(h5file);
                DataSet dataset = h5file.getRootGroup().openDataSet(name);
                int[] dims = dataset.getIntDims();
                dataset.close();
                data = DenseFactory.floatRowDirect(dims);
                LOGGER.info("Loaded data from {} {}", name, Arrays.toString(dims));
                reader.read(name, data);
                reader.close();
            }
            LOGGER.info("Calculating stats on UBM");
            GMMMAPStats ubmStats = new GMMMAPStats(ubm);
            List<int[]> indices = ubmStats.add(data.rowsIterator(), C);
            DiagCovGMM gmm = ubm.copy();
            for (int i = 1; i <= MAP_ITERATIONS; i++) {
                if (!GMMUtils.isGMMParametersFinite(gmm)) {
                    LOGGER.error("GMM for {} contains invalid parameters before iteration {}", name, i);
                    throw new RuntimeException();
                }
                GMMMAPStats stats = new GMMMAPStats(gmm);
                stats.add(data.rowsIterator(), indices);
                LOGGER.info("MAP iteration {}, log likelihood = {}", i, stats.getTotalLogLh());
                gmm.doMAPonMeans(stats, RELEVANCE);
                if (!GMMUtils.isGMMParametersFinite(gmm)) {
                    LOGGER.error("GMM for {} contains invalid parameters afer iteration {}", name, i);
                    throw new RuntimeException();
                }
            }
            FloatVector sv = GMMUtils.createSupervector(gmm, ubm);
            if (!FloatMatrixUtils.isAllFinite(sv)) {
                LOGGER.error("Supervector for {} contains invalid values", name);
                throw new RuntimeException();
            }
            return new SpeakerTrainResult(name, sv);
        }

        @Override
        public int compareTo(final SpeakerTrainJob other) {
            return name.compareTo(other.name);
        }
    }

    public static final class SpeakerTrainTask extends GridTaskAdapter<SpeakerTrainJob, SpeakerTrainResult> {
        private static final long serialVersionUID = 1L;

        private final Random rng = new Random();

        @Override
        public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final SpeakerTrainJob arg)
                throws GridException {
            Map<SpeakerTrainJob, GridNode> map = new HashMap<SpeakerTrainJob, GridNode>();
            map.put(arg, subgrid.get(rng.nextInt(subgrid.size())));
            return map;
        }

        @Override
        public SpeakerTrainResult reduce(List<GridJobResult> results) throws GridException {
            return results.get(0).getData();
        }
    }

    private static final int C = 5;

    private static final String DATA_FILE = "Z:/data/sre04mfcc_1s1s.h5";

    private static final String GMM_FILE = "Z:/data/sre04gmm_1s1s.h5";

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainBackground.class);

    private static final int MAP_ITERATIONS = 10;

    private static final float RELEVANCE = 16.0f;

    private static final String UBM_FILE = "Z:/data/ubm_floored_512_3.h5";

    public static void main(final String[] args) throws Exception {
        DiagCovGMM ubm = IOUtils.readDiagCovGMM(UBM_FILE);
        if (!GMMUtils.isGMMParametersFinite(ubm)) {
            LOGGER.error("UBM contains invalid parameters");
            throw new RuntimeException();
        }

        List<SpeakerTrainJob> jobs = new ArrayList<SpeakerTrainJob>();
        H5File dataFile = new H5File(DATA_FILE);
        for (DataSet dataset : dataFile.getRootGroup().getDataSets()) {
            String name = dataset.getName();
            dataset.close();
            jobs.add(new SpeakerTrainJob(name));
        }
        dataFile.close();
        Collections.sort(jobs);

        H5File gmmFile = new H5File(GMM_FILE, H5File.H5F_ACC_TRUNC);
        final HDFWriter writer = new HDFWriter(gmmFile);
        final List<SpeakerTrainResult> results = new ArrayList<SpeakerTrainResult>();
        ResultHandler<SpeakerTrainResult> resultHandler = new ResultHandler<SpeakerTrainResult>() {
            private int resultCount = 0;

            @Override
            public void onResult(final SpeakerTrainResult result) {
                results.add(result);
                LOGGER.info("Got result for {} [{}]", result.name, ++resultCount);
                synchronized (H5File.class) {
                    FloatDenseVector sv = DenseFactory.floatVector(result.sv, Direction.ROW, Storage.DIRECT);
                    writer.write(result.name, sv);
                }
            }
        };
        if (true) {
            new DefaultGrid<SpeakerTrainJob, SpeakerTrainResult>(SpeakerTrainTask.class, jobs, resultHandler).run();
        } else {
            new LocalGrid<SpeakerTrainJob, SpeakerTrainResult>(SpeakerTrainTask.class, jobs, resultHandler).run();
        }
        writer.close();
    }
}
