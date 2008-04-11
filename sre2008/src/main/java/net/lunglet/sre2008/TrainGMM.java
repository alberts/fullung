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
import net.lunglet.gridgain.DefaultGrid;
import net.lunglet.gridgain.ResultListener;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
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

public final class TrainGMM {
    public static final class Job implements GridJob {
        private static final long serialVersionUID = 1L;

        private static final DiagCovGMM UBM;

        static {
            // XXX this hack is here to work around issues with serialization of
            // DiagCovGMM using GridGain
//            String ubmFile = "Z:\\data\\hlda_ubm_final_512.h5";
            String ubmFile = "Z:\\data\\orig_ubm_final_512.h5";
            UBM = IOUtils.readDiagCovGMM(ubmFile);
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
            for (DataSet ds : group.getDataSets()) {
                names.add(ds.getName());
                ds.close();
            }
            group.close();
        }
        h5file.close();
        Collections.sort(names);
        return names;
    }

    public static void main(final String[] args) throws Exception {
        // XXX change ubm file at the top
//        String datah5 = "Z:\\data\\sre04_background_mfcc2_hlda.h5";
//        String gmmFile = "Z:\\data\\sre04_background_hlda_gmm.h5";
//        String datah5 = "Z:\\data\\sre05_1conv4w_1conv4w_mfcc2_hlda.h5";
//        String gmmFile = "Z:\\data\\sre05_1conv4w_1conv4w_hlda_gmm.h5";
        String datah5 = "Z:\\data\\sre04_background_mfcc2.h5";
        String gmmFile = "Z:\\data\\sre04_background_gmm.h5";

        List<String> names = getNames(datah5);
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
                LOGGER.info("Received GMM for {}", result.getName());
                // XXX synchronize here to avoid problems with multiple threads
                // calling into resultListener at the same time
                synchronized (H5File.class) {
                    String name = result.getName();
                    String groupName = name.split("/")[1];
                    if (!gmmh5.getRootGroup().existsGroup(groupName)) {
                        gmmh5.getRootGroup().createGroup(groupName);
                    }
                    float[] model = result.getModel();
                    FloatDenseVector v = DenseFactory.floatVector(model, Direction.ROW, Storage.DIRECT);
                    writer.write(result.getName(), v);
                }
            }
        };
        new DefaultGrid<Result>(tasks, resultListener).run();
        writer.close();
    }
}
