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
import net.lunglet.util.AssertUtils;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.gridgain.grid.GridTaskSession;
import org.gridgain.grid.resources.GridTaskSessionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainGMM {
    public static final class Job implements GridJob {
        private static final long serialVersionUID = 1L;

        private static final DiagCovGMM UBM;

        static {
            String ubmFile = "Z:/data/ubm_floored_512_3.h5";
            UBM = IOUtils.readDiagCovGMM(ubmFile);
            checkGMM(UBM);
        }

        private final String datah5;

        private final String name;

        @GridTaskSessionResource
        private GridTaskSession session = null;

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
            if (false) {
                DiagCovGMM ubm = (DiagCovGMM) session.getAttribute(UBM_KEY);
                AssertUtils.assertNotNull(ubm);
                return ubm;
            }
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

    public static final class Task extends GridTaskAdapter<Job, Result> {
        private static final long serialVersionUID = 1L;

        private final Random rng = new Random();

        @GridTaskSessionResource
        private GridTaskSession session = null;

        private final DiagCovGMM ubm;

        public Task(final DiagCovGMM ubm) {
            this.ubm = ubm;
        }

        @Override
        public Map<Job, GridNode> map(final List<GridNode> subgrid, final Job job) throws GridException {
            if (false) {
                session.setAttribute(UBM_KEY, ubm);
            }
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

    private static final String UBM_KEY = "ubm";

    private static void checkGMM(final DiagCovGMM gmm) {
        if (!GMMUtils.isGMMParametersFinite(gmm)) {
            LOGGER.error("GMM contains invalid parameters");
            throw new RuntimeException();
        }
    }

    private static List<String> getNames(final String h5) {
        List<String> names = new ArrayList<String>();
        H5File h5file = new H5File(h5);
//        for (DataSet ds : h5file.getRootGroup().getDataSets()) {
//            names.add(ds.getName());
//            ds.close();
//        }
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

    // XXX make sure this program is run with lots of heap... maybe a GG leak somewhere
    public static void main(final String[] args) throws Exception {
        if (false) {
            String ubmFile = "Z:/data/ubm_floored_512_3.h5";
            DiagCovGMM ubm = IOUtils.readDiagCovGMM(ubmFile);
            checkGMM(ubm);
        }
        DiagCovGMM ubm = null;

//        String datah5 = "Z:/data/sre05mfcc_1s1s.h5";
        String datah5 = "Z:/data/sre06_1s1s_mfcc.h5";
//        String datah5 = "Z:\\data\\sre04_nap_mfcc.h5";
//        String datah5 = "Z:\\data\\sre04_background_mfcc.h5";
        List<String> names = getNames(datah5);

//      String gmmFile = "Z:/data/sre05gmm_1s1s.h5";
        String gmmFile = "Z:/data/sre06_1s1s_gmm.h5";
//        String gmmFile = "Z:/data/sre04_nap_gmm.h5";
//        String gmmFile = "Z:/data/sre04_background_gmm.h5";
        final H5File gmmh5 = new H5File(gmmFile, H5File.H5F_ACC_RDWR);

        Task task = new Task(ubm);
        List<Job> jobs = new ArrayList<Job>();
        for (String name : names) {
            if (gmmh5.getRootGroup().existsDataSet(name)) {
                continue;
            }
            Job job = new Job(name, datah5);
            jobs.add(job);
        }

        final HDFWriter writer = new HDFWriter(gmmh5);
        ResultListener<Result> resultListener = new ResultListener<Result>() {
            @Override
            public void onResult(final Result result) {
                LOGGER.info("Received GMM for {}", result.getName());
                String name = result.getName();
                String groupName = name.split("/")[1];
                if (!gmmh5.getRootGroup().existsGroup(groupName)) {
                    gmmh5.getRootGroup().createGroup(groupName);
                }
                float[] model = result.getModel();
                FloatDenseVector v = DenseFactory.floatVector(model, Direction.ROW, Storage.DIRECT);
                writer.write(result.getName(), v);
            }
        };

        new DefaultGrid<Job, Result>(task, jobs, resultListener).run();

        writer.close();
    }
}
