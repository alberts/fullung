package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

// TODO another thing user might want to get via the handle is the
// dimensions of the data it points to, without loading the data
// (or maybe just loading a header)

// TODO look at readResolve/writeReplace

// TODO maybe use UUIDs to identify handles

public interface Handle2 extends Comparable<Handle2> {
    final class Score implements Comparable<Score> {
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

    FloatVector<?> getData();

    int getIndex();

    String getLabel();

    String getName();

    List<Score> getScores();

    void setScores(List<Score> scores);
}
