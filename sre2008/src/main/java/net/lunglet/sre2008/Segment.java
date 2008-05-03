package net.lunglet.sre2008;

import java.util.Properties;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Segment {
    private final int channel;

    private final String hdfName;

    private final String name;

    private final Properties properties = new Properties();

    public Segment(final String hdfName) {
        this.hdfName = hdfName;
        String[] parts = hdfName.split("/");
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append("/");
        for (int i = 1; i < parts.length - 1; i++) {
            nameBuilder.append(parts[i]);
            if (i < parts.length - 2) {
                nameBuilder.append("/");
            }
        }
        this.name = nameBuilder.toString();
        this.channel = 0;
    }

    public Segment(final String name, final int channel) {
        if (channel != 0 && channel != 1) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.channel = channel;
        this.hdfName = "/" + this.name + "/" + this.channel;
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
        this.hdfName = "/" + this.name + "/" + this.channel;
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
        return hdfName;
    }

    public final String getName() {
        return name;
    }

    public String getProperty(final String key) {
        return (String) properties.get(key);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(channel).toHashCode();
    }

    public void setProperty(final String key, final String value) {
        properties.put(key, value);
    }

    @Override
    public String toString() {
        return name + ":" + channel + " -> " + hdfName;
    }
}
