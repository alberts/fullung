package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class AbstractHandle2 implements Handle2, Serializable {
    private final int index;

    private final String label;

    private final String name;

    private List<Score> scores;

    private final int duration;

    public AbstractHandle2(final String name, final int index, final String label) {
        this(name, index, label, Integer.MIN_VALUE);
    }

    public AbstractHandle2(final String name, final int index, final String label, final int duration) {
        this.name = name;
        this.index = index;
        this.label = label;
        this.duration = duration;
    }

    public final int compareTo(final Handle2 o) {
        return getIndex() - o.getIndex();
    }

    public abstract FloatVector<?> getData();

    public final int getIndex() {
        return index;
    }

    public final String getLabel() {
        return label;
    }

    public final String getName() {
        return name;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Handle2)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Handle2 other = (Handle2) obj;
        return new EqualsBuilder().append(name, other.getName()).append(index, other.getIndex()).append(label,
            other.getLabel()).isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append(name).append(index).append(label).toHashCode();
    }

    public final List<Score> getScores() {
        if (scores == null) {
            throw new IllegalStateException();
        }
        return scores;
    }

    public final void setScores(final List<Score> scores) {
        this.scores = Collections.unmodifiableList(new ArrayList<Score>(scores));
    }

    public int getDuration() {
        if (duration == Integer.MIN_VALUE) {
            throw new IllegalStateException();
        }
        return duration;
    }
}
