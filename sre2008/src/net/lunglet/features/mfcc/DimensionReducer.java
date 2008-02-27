package net.lunglet.features.mfcc;

public final class DimensionReducer {
    public Features apply(final Features features) {
        float[][] values = features.getValues();
        float[][] newValues = new float[values.length][];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                continue;
            }
            newValues[i] = new float[values[i].length - 2];
            // copy C1 to C12, excluding C0 and log energy
            System.arraycopy(values[i], 1, newValues[i], 0, 12);
            // copy deltas and delta-deltas
            System.arraycopy(values[i], 14, newValues[i], 12, 28);
        }
        return features.replaceValues(newValues);
    }
}
