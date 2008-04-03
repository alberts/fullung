package net.lunglet.sre2008;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Segment {
    private final int channel;

    private final String name;

    public Segment(final String name, final int channel) {
        if (channel != 0 && channel != 1) {
            throw new IllegalArgumentException();
        }
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

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Segment)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Segment other = (Segment) obj;
        return new EqualsBuilder().append(name, other.name).append(channel, other.channel).isEquals();
    }

    public final String getChannel() {
        return channel == 0 ? "a" : "b";
    }

    public final String getHDFName() {
        return "/" + name + "/" + channel;
    }

    public final String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(channel).toHashCode();
    }

    @Override
    public String toString() {
        return name + ":" + channel;
    }
}
