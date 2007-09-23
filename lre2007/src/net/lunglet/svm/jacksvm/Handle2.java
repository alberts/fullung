package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import java.util.List;

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
