package net.lunglet.sre2008;

public final class Trial extends Segment {
    private final boolean target;

    public Trial(final String name, final int channel, final boolean target) {
        super(name, channel);
        this.target = target;
    }

    public Trial(final String name, final String channel, final String target) {
        super(name, channel);
        if (target.toLowerCase().startsWith("targ")) {
            this.target = true;
        } else if (target.toLowerCase().startsWith("non")) {
            this.target = false;
        } else {
            throw new IllegalArgumentException("invalid target");
        }
    }

    public boolean isTarget() {
        return target;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + target;
    }
}
