package net.lunglet.sre2008;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class Model implements Comparable<Model> {
    private final Gender gender;

    private final String id;

    private final Properties properties = new Properties();

    private final Set<Trial> test;

    private final List<Segment> train;

    public Model(final String id, final Gender gender, final Collection<Segment> train) {
        this(id, gender, train, null);
    }

    public Model(final String id, final Gender gender, final Collection<Segment> train, final Collection<Trial> test) {
        if (train.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.gender = gender;
        this.train = Collections.unmodifiableList(new ArrayList<Segment>(train));
        this.test = new HashSet<Trial>();
        if (test != null) {
            this.test.addAll(test);
        }
    }

    public Model(final String id, final Segment trainSegment) {
        this(id, null, Arrays.asList(trainSegment));
    }

    public void addTrial(final Trial trial) {
        test.add(trial);
    }

    @Override
    public int compareTo(final Model o) {
        return id.compareTo(o.id);
    }

    public Gender getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public String getProperty(final String key) {
        return (String) properties.get(key);
    }

    public Set<Trial> getTest() {
        return Collections.unmodifiableSet(test);
    }

    public List<Segment> getTrain() {
        return Collections.unmodifiableList(train);
    }

    public void setProperty(final String key, final String value) {
        properties.put(key, value);
    }

    @Override
    public String toString() {
        return id + " " + gender + " " + train + " " + test;
    }
}
