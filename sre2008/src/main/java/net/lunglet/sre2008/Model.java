package net.lunglet.sre2008;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Model {
    private final Gender gender;

    private final String id;

    private final List<Trial> test;

    private final String testCondition;

    private final List<Segment> train;

    private final String trainCondition;

    public Model(final String id, final Gender gender, final String trainCondition, final Collection<Segment> train,
            final String testCondition, final Collection<Trial> test) {
        if (train.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.gender = gender;
        this.trainCondition = trainCondition;
        this.train = Collections.unmodifiableList(new ArrayList<Segment>(train));
        this.testCondition = testCondition;
        if (test != null) {
            this.test = Collections.unmodifiableList(new ArrayList<Trial>(test));
        } else {
            this.test = null;
        }
    }

    public Model(final String id, final Segment trainSegment) {
        this(id, null, null, Arrays.asList(trainSegment), null, null);
    }

    public Gender getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public List<Trial> getTest() {
        return test;
    }

    public String getTestCondition() {
        return testCondition;
    }

    public List<Segment> getTrain() {
        return train;
    }

    public String getTrainCondition() {
        return trainCondition;
    }

    @Override
    public String toString() {
        return id + " " + gender + " " + train + " " + test;
    }
}
