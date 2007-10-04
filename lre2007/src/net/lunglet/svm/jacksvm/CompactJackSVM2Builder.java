package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.svm.CompactSimpleSvmBuilder;
import net.lunglet.svm.SimpleSvm;

public final class CompactJackSVM2Builder {
    private final List<CompactSimpleSvmBuilder> svmBuilders;
    
    private final List<String> targetLabels;

    public CompactJackSVM2Builder(final SimpleSvm[] svms, final List<String> targetLabels) {
        this.svmBuilders = new ArrayList<CompactSimpleSvmBuilder>();
        for (SimpleSvm svm : svms) {
            if (svm != null) {
                svmBuilders.add(svm.getCompactBuilder());     
            } else {
                svmBuilders.add(null);
            }
        }
        this.targetLabels = new ArrayList<String>(targetLabels);
//        AssertUtils.assertEquals(svmBuilders.size(), targetLabels.size());
    }

    public void present(final FloatVector<?> x, final int index) {
        for (CompactSimpleSvmBuilder svmBuilder : svmBuilders) {
            if (svmBuilder == null) {
                continue;
            }
            svmBuilder.present(x, index);
        }
    }

    public JackSVM2 build() {
        FloatDenseMatrix supportVectors = null;
        FloatDenseVector rhos = new FloatDenseVector(svmBuilders.size());
        for (int i = 0; i < svmBuilders.size(); i++) {
            CompactSimpleSvmBuilder svmBuilder = svmBuilders.get(i);
            if (svmBuilder == null) {
                continue;
            }
            SimpleSvm svm = svmBuilder.build();
            FloatVector<?> sv = svm.getSupportVector();
            if (supportVectors == null) {
                supportVectors = new FloatDenseMatrix(rhos.length(), sv.length(), Orientation.COLUMN, Storage.DIRECT);
            }
            supportVectors.setRow(i, sv);
            rhos.set(i, svm.getRho());
        }
        return new JackSVM2(supportVectors, rhos, targetLabels);
    }
}
