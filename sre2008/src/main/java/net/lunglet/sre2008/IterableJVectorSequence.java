package net.lunglet.sre2008;

import com.dvsoft.sv.toolbox.matrix.JVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.util.Iterator;
import net.lunglet.array4j.matrix.FloatVector;

public final class IterableJVectorSequence implements JVectorSequence {
    private final int dimension;

    private Iterator<? extends FloatVector> iter;

    private final Iterable<? extends FloatVector> iterable;

    private final int noVectors;

    public IterableJVectorSequence(final Iterable<? extends FloatVector> iterable) {
        this(iterable, true);
    }

    public IterableJVectorSequence(final Iterable<? extends FloatVector> iterable, final boolean calculateNoVectors) {
        this.iterable = iterable;
        this.dimension = iterable.iterator().next().length();
        this.iter = iterable.iterator();
        if (calculateNoVectors) {
            int count = 0;
            Iterator<? extends FloatVector> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                count++;
            }
            this.noVectors = count;
        } else {
            this.noVectors = -1;
        }
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public JVector getNextVector() {
        if (!iter.hasNext()) {
            return null;
        }
        return new JVector(iter.next().toArray());
    }

    @Override
    public int noVectors() {
        if (noVectors >= 0) {
            return noVectors;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        iter = iterable.iterator();
    }

    @Override
    public int skip(final int noVectors) {
        throw new UnsupportedOperationException();
    }
}
