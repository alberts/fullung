package net.lunglet.sre2008;

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
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.gridgain.LocalGrid;
import net.lunglet.gridgain.ResultListener;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainGMM {
    public static final class Job implements GridJob {
        private static final HDFReader READER = new HDFReader(16 * 1024 * 1024);

        private static final long serialVersionUID = 1L;

        private static final DiagCovGMM UBM;

        static {
            // XXX this hack is here to work around issues with serialization of
            // DiagCovGMM using GridGain
            UBM = IOUtils.readDiagCovGMM(Constants.UBM_FILE);
            checkGMM(UBM);
        }

        private final String datah5;

        private final String name;

        public Job(final String name, final String datah5) {
            this.name = name;
            this.datah5 = datah5;
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Result execute() throws GridException {
            DiagCovGMM ubm = getUBM();
            FloatDenseMatrix data = readData();
            final FloatVector sv = GMMTrainer.train(ubm, data.rowsIterator());
            return new Result(name, sv.toArray());
        }

        private DiagCovGMM getUBM() {
            return UBM;
        }

        private FloatDenseMatrix readData() {
            H5File h5file = new H5File(datah5);
            DataSet dataset = h5file.getRootGroup().openDataSet(name);
            int[] dims = dataset.getIntDims();
            dataset.close();
            FloatDenseMatrix data = DenseFactory.floatRowHeap(dims[0], dims[1]);
            LOGGER.info("Loaded data from {} {}", name, Arrays.toString(dims));
            synchronized (READER) {
                READER.read(name, data);
            }
            h5file.close();
            return data;
        }
    }

    public static final class Result implements Serializable {
        private static final long serialVersionUID = 1L;

        private final float[] model;

        private final String name;

        public Result(final String name, final float[] model) {
            this.name = name;
            this.model = model;
        }

        public float[] getModel() {
            return model;
        }

        public String getName() {
            return name;
        }
    }

    public static final class Task extends GridTaskAdapter<Object, Result> {
        private static final long serialVersionUID = 1L;

        private final Job job;

        private final Random rng = new Random();

        public Task(final String name, final String datah5) {
            this.job = new Job(name, datah5);
        }

        @Override
        public Map<Job, GridNode> map(final List<GridNode> subgrid, final Object arg) throws GridException {
            Map<Job, GridNode> map = new HashMap<Job, GridNode>();
            map.put(job, subgrid.get(rng.nextInt(subgrid.size())));
            return map;
        }

        @Override
        public Result reduce(final List<GridJobResult> results) throws GridException {
            return results.get(0).getData();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainGMM.class);

    public static void checkGMM(final DiagCovGMM gmm) {
        if (!GMMUtils.isGMMParametersFinite(gmm)) {
            LOGGER.error("GMM contains invalid parameters");
            throw new RuntimeException();
        }
    }

    public static List<String> getNames(final String h5) {
        List<String> names = new ArrayList<String>();
        H5File h5file = new H5File(h5);
        for (Group group : h5file.getRootGroup().getGroups()) {
            for (Group group2 : group.getGroups()) {
                for (DataSet ds : group2.getDataSets()) {
                    names.add(ds.getName());
                    ds.close();
                }
                group2.close();
            }
            for (DataSet ds : group.getDataSets()) {
                names.add(ds.getName());
                ds.close();
            }
            group.close();
        }
        for (DataSet ds : h5file.getRootGroup().getDataSets()) {
            names.add(ds.getName());
            ds.close();
        }
        h5file.close();
        Collections.sort(names);
        return names;
    }

    public static void main(final String[] args) throws Exception {
        final String datah5;
        final String gmmFile;
        if (false) {
            datah5 = Constants.SVM_BACKGROUND_DATA;
            gmmFile = Constants.SVM_BACKGROUND_GMM;
        } else if (false) {
            datah5 = Constants.EVAL_DATA;
            gmmFile = Constants.EVAL_GMM;
        } else if (true) {
            datah5 = Constants.TNORM_DATA;
            gmmFile = Constants.TNORM_GMM;
        } else {
            throw new NotImplementedException();
        }

        List<String> names = getNames(datah5);
        LOGGER.info("Training {} GMM models", names.size());
        final H5File gmmh5 = new H5File(gmmFile, H5File.H5F_ACC_TRUNC);
        List<Task> tasks = new ArrayList<Task>();
        for (String name : names) {
            if (gmmh5.getRootGroup().existsDataSet(name)) {
                continue;
            }
            Task task = new Task(name, datah5);
            tasks.add(task);
        }

        final HDFWriter writer = new HDFWriter(gmmh5);
        ResultListener<Result> resultListener = new ResultListener<Result>() {
            @Override
            public void onResult(final Result result) {
                writeResult(gmmh5, writer, result);
            }
        };
        new LocalGrid<Result>(tasks, resultListener).run();
        // TODO close all output files before doing any kind of shutdown
        writer.close();
    }

    public static final void writeResult(final H5File gmmh5, final HDFWriter writer, final Result result) {
        LOGGER.info("Received GMM for {}", result.getName());
        String name = result.getName();
        String[] parts = name.split("/");
        // this has to be synchronized to prevent multiple threads calling
        // the result listeners from trying to create groups that already exist
        synchronized (TrainGMM.class) {
            for (int i = 1; i < parts.length - 1; i++) {
                StringBuilder pathBuilder = new StringBuilder();
                pathBuilder.append("/");
                for (int j = 1; j <= i; j++) {
                    pathBuilder.append(parts[j]);
                    if (j < i) {
                        pathBuilder.append("/");
                    }
                }
                String path = pathBuilder.toString();
                if (!gmmh5.getRootGroup().existsGroup(path)) {
                    LOGGER.info("Creating group {}", path);
                    gmmh5.getRootGroup().createGroup(path);
                }
            }
        }
        float[] model = result.getModel();
        FloatDenseVector v = DenseFactory.floatVector(model, Direction.ROW, Storage.DIRECT);
        writer.write(result.getName(), v);
    }
}
