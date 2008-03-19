package net.lunglet.sre2008;

public class Segment {
    private final int channel;

    private final String name;

    public Segment(final String name, final int channel) {
        this.name = name;
        this.channel = channel;
    }

    public Segment(final String name, final String channel) {
        this.name = name;
        if (channel.toLowerCase().equals("a")) {
            this.channel = 0;
        } else if (channel.toLowerCase().equals("b")) {
            this.channel = 1;
        } else {
            throw new IllegalArgumentException("invalid channel");
        }
    }

    public final String getHDFName() {
        return "/" + name + "/" + channel;
    }

    @Override
    public String toString() {
        return name + ":" + channel;
    }
}
