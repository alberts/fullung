package net.lunglet.sre2008.svm;

import java.io.Serializable;
import net.lunglet.array4j.matrix.FloatVector;

public final class SpeakerTrainResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;

    private final float[] sv;

    public SpeakerTrainResult(final String name, final FloatVector sv) {
        this.name = name;
        // XXX using a float array for now because FloatVectors don't
        // serialize properly yet
        this.sv = sv.toArray();
    }

    public float[] getModel() {
        return sv;
    }

    public String getName() {
        return name;
    }
}
