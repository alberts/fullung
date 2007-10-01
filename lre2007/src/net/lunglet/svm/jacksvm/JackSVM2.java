package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.lunglet.svm.Handle;
import net.lunglet.svm.PrecomputedKernel;
import net.lunglet.svm.SimpleSvm;
import net.lunglet.svm.SvmNode;
import net.lunglet.svm.jacksvm.Handle2.Score;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO make cost at training configurable

public final class JackSVM2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final transient KernelReader kernelReader;

    // XXX this logger is transient, so with the code as-is, it's not going to
    // get restored when a JackSVM2 is deserialized. Need to look into a way of
    // dealing with this problem in general. Maybe having classes that need to
    // contain a logger and also be serializable are symptomatic of a bad design
    // though...
    private transient Log log = LogFactory.getLog(JackSVM2.class);

    private FloatDenseVector rhos;

    private FloatDenseMatrix supportVectors;

    private List<String> targetLabels;

    private SimpleSvm[] originalSvms;

    private transient List<Handle2> trainData;

    public void setTrainData(final List<Handle2> trainData) {
        this.trainData = trainData;
    }

    public JackSVM2(final FloatDenseMatrix supportVectors, final FloatDenseVector rhos, final List<String> targetLabels) {
        this.kernelReader = null;
        this.supportVectors = supportVectors;
        this.rhos = rhos;
        this.targetLabels = targetLabels;
        this.trainData = null;
    }

    public JackSVM2(final KernelReader kernelReader) {
        this.kernelReader = kernelReader;
        this.trainData = null;
    }

    private PrecomputedKernel createPrecomputedKernel(final int[] indexes) {
        if (kernelReader == null) {
            throw new IllegalStateException();
        }
        return new PrecomputedKernel() {
            @Override
            public float get(final int i, final int j) {
                return kernelReader.read(indexes[i], indexes[j]);
            }
        };
    }

    public SvmNode[] getSvmNodes() {
        if (originalSvms == null) {
            throw new IllegalStateException("SvmNodes only available before compaction");
        }
        List<SvmNode> svmNodes = new ArrayList<SvmNode>();
        for (SimpleSvm svm : originalSvms) {
            for (SvmNode node : svm.getSvmNodes()) {
                // map from svm assigned index to data index
                node.setIndex(trainData.get(node.getIndex()).getIndex());
            }
            Collections.addAll(svmNodes, svm.getSvmNodes());
        }
        return svmNodes.toArray(new SvmNode[0]);
    }

    private PrecomputedKernel createPrecomputedKernel2(final int[] indexes) {
        log.info("reading kernel for " + indexes.length + " entries");
        PrecomputedKernel kernel = new PrecomputedKernel() {
            private final FloatPackedMatrix kernel = kernelReader.read(indexes);

            @Override
            public float get(final int i, final int j) {
                return kernel.get(i, j);
            }
        };
        log.info("kernel reading done");
        return kernel;
    }

    public List<String> getTargetLabels() {
        return Collections.unmodifiableList(targetLabels);
    }

    public void score(final List<Handle2> testData) {
        if (supportVectors == null || rhos == null) {
            throw new IllegalStateException();
        }
        for (int i = 0; i < testData.size(); i++) {
            Handle2 handle = testData.get(i);
            // TODO read testData into buffer so that we can score with a gemm
            FloatDenseMatrix result = FloatMatrixMath.times(supportVectors, handle.getData());
            List<Score> scores = new ArrayList<Score>(supportVectors.rows());
            for (int j = 0; j < result.rows(); j++) {
                float score = result.get(j, 0) - rhos.get(j);
                scores.add(new Score(targetLabels.get(j), score));
            }
            handle.setScores(scores);
        }
    }

    public void compact() {
        if (originalSvms == null) {
            throw new IllegalStateException();
        }
        // don't need this anymore
        trainData = null;
        for (int i = 0; i < originalSvms.length; i++) {
            log.info("compacting and extracting model");
            SimpleSvm svm = originalSvms[i];
            svm.compact();
            FloatVector<?> sv = svm.getSupportVector();
            if (supportVectors == null) {
                final int svrows = rhos.length();
                final int svcols = sv.length();
                this.supportVectors = new FloatDenseMatrix(svrows, svcols, Orientation.COLUMN, Storage.DIRECT);
            }
            supportVectors.setRow(i, sv);
            rhos.set(i, svm.getRho());
        }
        originalSvms = null;
    }

    public FloatDenseMatrix getModels() {
        if (supportVectors == null || rhos == null) {
            throw new IllegalStateException();
        }
        FloatDenseMatrix models = new FloatDenseMatrix(supportVectors.rows(), supportVectors.columns() + 1,
                Orientation.ROW, Storage.DIRECT);
        for (int i = 0; i < supportVectors.rows(); i++) {
            for (int j = 0; j < supportVectors.columns(); j++) {
                models.set(i, j, supportVectors.get(i, j));
            }
        }
        for (int i = 0; i < rhos.length(); i++) {
            models.set(i, models.columns() - 1, rhos.get(i));
        }
        return models;
    }

    private SimpleSvm train(final String targetLabel, final List<Handle2> data, final PrecomputedKernel kernel) {
        log.info("training with target label " + targetLabel);
        List<Handle> dataList = new ArrayList<Handle>();
        for (final Handle2 x : data) {
            dataList.add(new Handle() {
                @Override
                public FloatVector<?> getData() {
                    return x.getData();
                }

                @Override
                public int getLabel() {
                    // TODO maybe do 1 and -1 instead
                    return x.getLabel().equals(targetLabel) ? 0 : 1;
                }
            });
        }
        SimpleSvm svm = new SimpleSvm(dataList, kernel);
        svm.train(100.0);
        return svm;
    }

    public void train(final List<Handle2> trainData) {
        final int[] indexes = new int[trainData.size()];
        Set<String> uniqueLabels = new HashSet<String>();
        for (int i = 0; i < indexes.length; i++) {
            Handle2 handle = trainData.get(i);
            indexes[i] = handle.getIndex();
            uniqueLabels.add(handle.getLabel());
        }
        PrecomputedKernel kernel = createPrecomputedKernel(indexes);
        List<String> labelsList = new ArrayList<String>(uniqueLabels);
        Collections.sort(labelsList);
        int p = labelsList.size() <= 2 ? 1 : labelsList.size();
        this.rhos = new FloatDenseVector(p);
        this.targetLabels = new ArrayList<String>(p);
        this.originalSvms = new SimpleSvm[p];
        for (int i = 0; i < p; i++) {
            String targetLabel = labelsList.get(i);
            originalSvms[i] = train(targetLabel, trainData, kernel);
            targetLabels.add(targetLabel);
        }
        this.trainData = trainData;
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.log = LogFactory.getLog(JackSVM2.class);
    }
}
