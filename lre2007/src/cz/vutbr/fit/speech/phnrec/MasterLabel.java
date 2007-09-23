package cz.vutbr.fit.speech.phnrec;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class MasterLabel {
    final long endTime;

    final String label;

    final float score;

    final long startTime;

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

    public long getDuration() {
        return endTime - startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
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
