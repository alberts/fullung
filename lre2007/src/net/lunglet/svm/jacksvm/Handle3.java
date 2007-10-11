package net.lunglet.svm.jacksvm;

import java.io.Serializable;
import java.util.List;

import com.googlecode.array4j.FloatVector;

public final class Handle3 implements Handle2, Serializable {
    private static final long serialVersionUID = 1L;

    private final int index;

    private final String label;

    private final String name;

    public Handle3(final int index, final String label, final String name) {
        this.index = index;
        this.label = label;
        this.name = name;
    }

    @Override
    public FloatVector<?> getData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Score> getScores() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setScores(List<Score> scores) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Handle2 o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDuration() {
        throw new UnsupportedOperationException();
    }
}
