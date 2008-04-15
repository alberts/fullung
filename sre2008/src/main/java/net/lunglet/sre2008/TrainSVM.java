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
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.array4j.matrix.packed.FloatPackedMatrix;
import net.lunglet.array4j.matrix.packed.PackedFactory;
import net.lunglet.gridgain.LocalGrid;
import net.lunglet.gridgain.ResultListener;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.svm.SpeakerKernelMatrix;
import net.lunglet.svm.Handle;
import net.lunglet.svm.SvmClassifier;
import net.lunglet.util.ArrayMath;
import net.lunglet.util.AssertUtils;
import org.apache.commons.lang.NotImplementedException;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainSVM {
    private static final class HDFHandle implements Handle, Serializable {
        private static final long serialVersionUID = 1L;

        private transient FloatDenseVector data;

        private final String h5;

        private final int index;

        private final int label;

        private final String name;

        public HDFHandle(final String h5, final String name, final int index, final int label) {
            this.h5 = h5;
            this.name = name;
            this.index = index;
            this.label = label;
            this.data = null;
        }

        @Override
        public synchronized FloatVector getData() {
            if (data != null) {
                return data;
            }
            H5File h5file = new H5File(h5);
            HDFReader reader = new HDFReader(h5file);
            DataSet dataset = h5file.getRootGroup().openDataSet(name);
            int[] dims = dataset.getIntDims();
            dataset.close();
            if (dims.length > 2 || (dims.length == 2 && dims[0] > 1 && dims[1] > 1)) {
                throw new RuntimeException();
            }
            // XXX can't use direct buffers for all this stuff
            data = DenseFactory.floatRowDirect(ArrayMath.max(dims));
            LOGGER.info("Loading background data from {} {}", name, Arrays.toString(dims));
            reader.read(name, data);
            reader.close();
            return data;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public int getLabel() {
            return label;
        }
    }

    public static final class Job implements GridJob {
        private static final long serialVersionUID = 1L;

        private final double cost;

        private final String datah5;

        private final String modelId;

        private final String name;

        public Job(final String modelId, final String name, final String datah5, final double cost) {
            this.modelId = modelId;
            this.name = name;
            this.datah5 = datah5;
            this.cost = cost;
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Result execute() throws GridException {
            FloatMatrix background = getKernel();
            final FloatVector sv = readData();
            List<Handle> backgroundData = getBackgroundData();

            // do remaining kernel evaluations
            FloatVector speaker = DenseFactory.floatColumn(backgroundData.size() + 1);
            for (int i = 0; i < backgroundData.size(); i++) {
                FloatVector x = backgroundData.get(i).getData();
                speaker.set(i, FloatMatrixMath.dot(sv, x));
            }
            speaker.set(speaker.length() - 1, FloatMatrixMath.dot(sv, sv));

            // create complete kernel matrix
            SpeakerKernelMatrix kernel = new SpeakerKernelMatrix(background, speaker);

            final int index = backgroundData.size();
            List<Handle> svmData = new ArrayList<Handle>(backgroundData);
            svmData.add(new Handle() {
                @Override
                public FloatVector getData() {
                    return sv;
                }

                @Override
                public int getIndex() {
                    return index;
                }

                @Override
                public int getLabel() {
                    // use label 0 here to make signs come out right
                    return 0;
                }
            });
            SvmClassifier svm = new SvmClassifier(svmData, kernel);
            svm.train(cost);
            SvmClassifier compactSvm = svm.compact();
            FloatVector model = compactSvm.getModel();

            // check scores
            float targetScore = compactSvm.score(sv.transpose()).get(0, 0);
            AssertUtils.assertTrue(targetScore > 0);
            float[] scores = compactSvm.score(backgroundData).toArray();
            for (int i = 0; i < scores.length - 1; i++) {
                AssertUtils.assertTrue(scores[i] < 0);
            }

            // Z-Norm using background data (might need other data)
            if (false) {
                float meanScore = 0.0f;
                for (int i = 0; i < scores.length - 1; i++) {
                    meanScore += (scores[i] - meanScore) / (i + 1);
                }
                float rho = model.get(model.length() - 1);
                model.set(model.length() - 1, rho + meanScore);
            }

            return new Result(modelId, model);
        }

        private FloatMatrix getKernel() {
            return Task.KERNEL;
        }

        private List<Handle> getBackgroundData() {
            return Task.BACKGROUND_DATA;
        }

        private FloatDenseVector readData() {
            H5File h5file = new H5File(datah5);
            HDFReader reader = new HDFReader(h5file);
            DataSet dataset = h5file.getRootGroup().openDataSet(name);
            int[] dims = dataset.getIntDims();
            dataset.close();
            FloatDenseVector data = DenseFactory.floatRowDirect(ArrayMath.max(dims));
            LOGGER.info("Loading data from {} {}", name, Arrays.toString(dims));
            reader.read(name, data);
            reader.close();
            return data;
        }
    }

    public static final class Result implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;

        private final float[] model;

        public Result(final String id, final FloatVector model) {
            this.id = id;
            this.model = model.toArray();
        }

        public String getId() {
            return id;
        }

        public float[] getModel() {
            return model;
        }
    }

    public static final class Task extends GridTaskAdapter<Object, Result> {
        private static final FloatPackedMatrix KERNEL;

        private static final long serialVersionUID = 1L;

        public static final List<Handle> BACKGROUND_DATA;

        static {
            // background data
            String svmFile = Constants.SVM_BACKGROUND_GMM;
            List<Handle> temp = new ArrayList<Handle>();
            List<String> names = getNames(svmFile);
            int index = 0;
            for (String name : names) {
                // use label 1 here to make signs come out right
                temp.add(new HDFHandle(svmFile, name, index++, 1));
            }
            BACKGROUND_DATA = Collections.unmodifiableList(temp);

            // kernel
            HDFReader kernelReader = new HDFReader(Constants.KERNEL_FILE);
            // TODO figure out this dimension automatically
            KERNEL = PackedFactory.floatSymmetricDirect(1790);
            kernelReader.read("/kernel", KERNEL);
        }

        private final Random rng = new Random();

        private final Job job;

        public Task(final Job job) {
            this.job = job;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainSVM.class);

    private static List<String> getNames(final String h5) {
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
        h5file.close();
        Collections.sort(names);
        return names;
    }

    public static void main(final String[] args) throws Exception {
        final List<Model> models;
        final String gmmFile;
        final String svmFile;
        if (false) {
            models = Evaluation2.readModels(Constants.EVAL_FILE);
            gmmFile = Constants.EVAL_GMM;
            svmFile = Constants.EVAL_SVM;
        } else if (true) {
            gmmFile = Constants.TNORM_GMM;
            models = new ArrayList<Model>();
            int i = 0;
            for (String name : getNames(gmmFile)) {
                models.add(new Model("tnorm" + i++, new Segment(name)));
            }
            svmFile = Constants.TNORM_SVM;
        } else {
            throw new NotImplementedException();
        }

        LOGGER.info("Checking data file");
        H5File trainh5 = new H5File(gmmFile);
        Evaluation2.checkData(trainh5, models);
        trainh5.close();

        final double cost = 1000.0;
        List<Task> tasks = new ArrayList<Task>();
        for (Model model : models) {
            List<Segment> train = model.getTrain();
            AssertUtils.assertEquals(1, train.size());
            Job job = new Job(model.getId(), train.get(0).getHDFName(), gmmFile, cost);
            tasks.add(new Task(job));
        }

        final H5File svmh5 = new H5File(svmFile, H5File.H5F_ACC_TRUNC);
        final HDFWriter writer = new HDFWriter(svmh5);
        ResultListener<Result> resultListener = new ResultListener<Result>() {
            @Override
            public void onResult(final Result result) {
                LOGGER.info("Received result for {}", result.getId());
                String id = result.getId();
                float[] model = result.getModel();
                FloatDenseVector v = DenseFactory.floatVector(model, Direction.ROW, Storage.DIRECT);
                writer.write(id, v);
            }
        };

        new LocalGrid<Result>(tasks, resultListener).run();

        writer.close();
    }
}
