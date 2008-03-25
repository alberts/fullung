package net.lunglet.sre2008.svm;

import net.lunglet.array4j.matrix.AbstractMatrix;
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.util.FloatMatrixUtils;

public final class SpeakerKernelMatrix extends AbstractMatrix<FloatVector> implements FloatMatrix {
    private static final long serialVersionUID = 1L;

    private final FloatMatrix background;

    private final FloatMatrix speaker;

    public SpeakerKernelMatrix(final FloatMatrix background, final FloatMatrix speaker) {
        super(speaker.rows(), background.columns() + speaker.columns());
        if (speaker.rows() < background.rows() + 1) {
            throw new IllegalArgumentException();
        }
        if (speaker.columns() != speaker.rows() - background.rows()) {
            throw new IllegalArgumentException();
        }
        this.background = background;
        this.speaker = speaker;
    }

    @Override
    public FloatVector column(int column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float get(int row, int column) {
        if (row > column) {
            return get(column, row);
        }
        if (row < background.rows() && column < background.columns()) {
            return background.get(row, column);
        }
        return speaker.get(row, column - background.columns());
    }

    @Override
    public FloatVector row(int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int row, int column, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColumn(int column, FloatVector columnVector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRow(int row, FloatVector rowVector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[][] toColumnArrays() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float[][] toRowArrays() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return FloatMatrixUtils.toString(this);
    }

    @Override
    public FloatMatrix transpose() {
        return this;
    }
}
