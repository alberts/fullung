package cz.vutbr.fit.speech.phnrec;

public final class Segment {
    final long endTime;

    final long startTime;

    public Segment(final long startTime, final long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean contains(final long timestamp) {
        return timestamp >= startTime && timestamp <= endTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return String.format("%d %d", startTime, endTime);
    }
}
