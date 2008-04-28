package net.lunglet.sre2008;

import com.dvsoft.sv.toolbox.gmm.EigenMapTrain;
import com.dvsoft.sv.toolbox.gmm.FrameLvlBgr;
import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.gmm.SuperVector;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gridgain.DefaultGrid;
import net.lunglet.gridgain.ResultListener;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.TrainGMM.Result;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.util.Converters;
import org.apache.commons.lang.NotImplementedException;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainGMM2 {
    private static final class Job implements GridJob {
        private static final HDFReader READER = new HDFReader(16 * 1024 * 1024);

        private static final JMatrix CHANNEL_SPACE;

        private static final long serialVersionUID = 1L;

        // XXX looks like a potential place for cache line ping pong
        // XXX each thread should probably copy the UBM before doing work?
        private static final JMapGMM UBM;

        static {
            String ubmFile = Constants.UBM_FILE;
            DiagCovGMM ubm = IOUtils.readDiagCovGMM(ubmFile);
            TrainGMM.checkGMM(ubm);
            UBM = Converters.convert(ubm);
            if (true) {
                String umatFile = Constants.CHANNEL_FILE;
                HDFReader reader = new HDFReader(umatFile);
                int dim = 512 * 38;
                int k = 40;
                FloatDenseMatrix channelSpace = DenseFactory.floatRowDirect(new int[]{dim, k});
                reader.read("/U", channelSpace);
                reader.close();
                CHANNEL_SPACE = new JMatrix(channelSpace.toRowArrays());
            } else {
                CHANNEL_SPACE = null;
            }
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
            JVectorSequence data = new IterableJVectorSequence(readData().rowsIterator(), true);
            JMapGMM ubm = getUBM();
            LOGGER.info("Evaluating frame level background");
            FrameLvlBgr bgr = ubm.evalFrameLvlBgr(data);

            JMatrix u = getChannelSpace();
            final JVectorSequence fixedData;
            if (u != null) {
                LOGGER.info("Training channel factors");
                EigenMapTrain channelTrain = new EigenMapTrain(u, ubm);
                channelTrain.setData(data, bgr);
                channelTrain.channelEMIteration();
                SuperVector ux = channelTrain.getFeatureUx();
                LOGGER.info("Performing feature space channel compensation");
                data.reset();
                int i = 0;
                List<FloatVector> fixedDataList = new ArrayList<FloatVector>();
                JVector dataVector = data.getNextVector();
                while (dataVector != null) {
                    int top1 = bgr.getMixtureVector(i++)[0];
                    JVector uxpart = ux.subVectors[top1];
                    JVector x = dataVector.minus(uxpart);
                    fixedDataList.add(DenseFactory.floatVector(x.toDoubles()));
                    dataVector = data.getNextVector();
                }
                fixedData = new IterableJVectorSequence(fixedDataList);
            } else {
                fixedData = data;
            }

            LOGGER.info("Training speaker model");
            EigenMapTrain speakerTrain = new EigenMapTrain(null, ubm);
            speakerTrain.setData(fixedData, bgr);
            for (int iter = 1; iter <= MAP_ITERATIONS; iter++) {
                double ll = speakerTrain.EMIteration(false);
                LOGGER.info("MAP iteration {}, log likelihood = {}", iter, ll);
            }

            // return supervector minus UBM, divided by standard deviation
            return new Result(name, speakerTrain.getSModel().data);
        }

        private JMatrix getChannelSpace() {
            return CHANNEL_SPACE;
        }

        private JMapGMM getUBM() {
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
                READER.read(h5file, name, data);
            }
            h5file.close();
            return data;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainGMM2.class);

    private static final int MAP_ITERATIONS = 10;

    public static void main(final String[] args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        final String datah5;
        final String gmmFile;
        final List<String> names;

        // TODO build abstraction layer that takes filenames and maps them to something sensible
        // for evaluation data, check that filenames are unique
        // for tnorm data, map files to tnorm0, tnorm1 etc

        if (false) {
            datah5 = Constants.NAP_DATA;
            names = TrainGMM.getNames(datah5);
            gmmFile = Constants.NAP_GMM;
        } else if (false) {
            datah5 = Constants.SVM_BACKGROUND_DATA;
            names = TrainGMM.getNames(datah5);
            gmmFile = Constants.SVM_BACKGROUND_GMM;
        } else if (false) {
            datah5 = Constants.TNORM_DATA;
            names = TrainGMM.getNames(datah5);
            gmmFile = Constants.TNORM_GMM;
        } else if (true) {
            datah5 = Constants.EVAL_DATA;
            Set<String> namesSet = new HashSet<String>();
            for (Model model : Evaluation2.readModels(Constants.EVAL_FILE)) {
                for (Segment segment : model.getTrain()) {
                    namesSet.add(segment.getHDFName());
                }
                for (Segment segment : model.getTest()) {
                    namesSet.add(segment.getHDFName());
                }
            }
            names = new ArrayList<String>(namesSet);
            Collections.sort(names);
            gmmFile = Constants.EVAL_GMM;
        } else {
            throw new NotImplementedException();
        }

        if (new File(gmmFile).exists()) {
            throw new RuntimeException("Output file " + gmmFile + " already exists");
        }

        H5File mfcch5 = new H5File(datah5);
        List<Task> tasks = new ArrayList<Task>();
        for (String name : names) {
//            if (gmmh5.getRootGroup().existsDataSet(name)) {
//                continue;
//            }
            if (!mfcch5.getRootGroup().existsDataSet(name)) {
                LOGGER.error("{} is missing", name);
                System.exit(1);
            }
            Task task = new Task(name, datah5);
            tasks.add(task);
        }
        mfcch5.close();

        LOGGER.info("{} GMM training tasks to do", tasks.size());
        final H5File gmmh5 = new H5File(gmmFile, H5File.H5F_ACC_TRUNC);
        final HDFWriter writer = new HDFWriter(gmmh5);
        ResultListener<Result> resultListener = new ResultListener<Result>() {
            @Override
            public void onResult(final Result result) {
                // TODO result are going missing
                TrainGMM.writeResult(gmmh5, writer, result);
            }
        };
        new DefaultGrid<Result>(tasks, resultListener).run();
        writer.close();
    }
}
