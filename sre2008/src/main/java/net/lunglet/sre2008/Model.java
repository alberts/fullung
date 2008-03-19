package net.lunglet.sre2008;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Model {
    private final Gender gender;

    private final String id;

    private final List<Segment> test;

    private final List<Segment> train;

    public Model(final String id, final Gender gender, final Collection<Segment> train, final Collection<Trial> test) {
        if (train.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.gender = gender;
        this.train = Collections.unmodifiableList(new ArrayList<Segment>(train));
        this.test = Collections.unmodifiableList(new ArrayList<Segment>(test));
    }

    public Gender getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public List<Segment> getTest() {
        return test;
    }

    public List<Segment> getTrain() {
        return train;
    }

    @Override
    public String toString() {
        return id + " " + gender + " " + train + " " + test;
    }
}
