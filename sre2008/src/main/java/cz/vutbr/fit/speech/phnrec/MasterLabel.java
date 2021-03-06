package cz.vutbr.fit.speech.phnrec;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class MasterLabel {
    private final long endTime;

    private final String label;

    private final float score;

    private final long startTime;

    public MasterLabel(final String label, final long startTime, final long endTime, final float score) {
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof MasterLabel)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MasterLabel other = (MasterLabel) obj;
        return new EqualsBuilder().append(label, other.label).append(startTime, other.startTime).append(endTime,
                other.endTime).append(score, other.score).isEquals();
    }

    public double getDuration() {
        return getEndTime() - getStartTime();
    }

    public long getDurationHTK() {
        return getEndTimeHTK() - getStartTimeHTK();
    }

    public double getEndTime() {
        return endTime / 1.0e7;
    }

    public long getEndTimeHTK() {
        return endTime;
    }

    public String getLabel() {
        return label;
    }

    public float getScore() {
        return score;
    }

    public double getStartTime() {
        return startTime / 1.0e7;
    }

    public long getStartTimeHTK() {
        return startTime;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(label).append(startTime).append(endTime).append(score).toHashCode();
    }

    public boolean isValid() {
        return !label.equals("int") && !label.equals("oth") && !label.equals("pau") && !label.equals("spk");
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %.6f", startTime, endTime, label, score);
    }
}
