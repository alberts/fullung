package net.lunglet.svm.jacksvm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lunglet.svm.Handle;
import net.lunglet.svm.PrecomputedKernel;
import net.lunglet.svm.SimpleSvm;
import net.lunglet.svm.jacksvm.Handle2.Score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import com.googlecode.array4j.packed.FloatPackedMatrix;

// TODO make cost at training configurable

public final class JackSVM2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final transient KernelReader kernelReader;

    // XXX this logger is transient, so with the code as-is, it's not going to
    // get restored when a JackSVM2 is deserialized. Need to look into a way of
    // dealing with this problem in general. Maybe having classes that need to
    // contain a logger and also be serializable are symptomatic of a bad design
    // though...
    private final transient Log log = LogFactory.getLog(JackSVM.class);

    private FloatDenseVector rhos;

    private FloatDenseMatrix supportVectors;

    private List<String> targetLabels;

    private SimpleSvm[] originalSvms;

    public JackSVM2(final FloatDenseMatrix supportVectors, final FloatDenseVector rhos, final List<String> targetLabels) {
        this.kernelReader = null;
        this.supportVectors = supportVectors;
        this.rhos = rhos;
        this.targetLabels = targetLabels;
    }

    public JackSVM2(final KernelReader kernelReader) {
        this.kernelReader = kernelReader;
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

    public FloatDenseVector getRhos() {
        if (rhos == null) {
            throw new IllegalStateException();
        }
        return rhos;
    }

    public FloatDenseMatrix getSupportVectors() {
        if (supportVectors == null) {
            throw new IllegalStateException();
        }
        return supportVectors;
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
            break;
        }
        originalSvms = null;
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
            break;
        }
    }
}
