package net.lunglet.features.mfcc;

public final class DimensionReducer {
    public FeatureSet apply(final FeatureSet features) {
        float[][] values = features.getValues();
        float[][] newValues = new float[values.length][];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                continue;
            }
            newValues[i] = new float[values[i].length - 1];
            // copy C1 to C12, excluding log energy
            System.arraycopy(values[i], 0, newValues[i], 0, 12);
            // copy deltas and delta-deltas, including those for log energy
            System.arraycopy(values[i], 13, newValues[i], 12, 2 * 13);
        }
        return features.replaceValues(newValues);
    }
}
