package net.lunglet.svm.jacksvm;

import org.apache.commons.lang.builder.EqualsBuilder;

public final class Score implements Comparable<Score> {
    private final String label;

    private final float score;

    public Score(final String label, final float score) {
        this.label = label;
        this.score = score;
    }

    @Override
    public int compareTo(final Score o) {
        return label.compareTo(o.label);
    }

    public String getLabel() {
        return label;
    }

    public float getScore() {
        return score;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Score)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Score other = (Score) obj;
        return new EqualsBuilder().append(score, other.score).append(label, other.label).isEquals();
    }

    @Override
    public String toString() {
        return label + "=" + score;
    }
}
