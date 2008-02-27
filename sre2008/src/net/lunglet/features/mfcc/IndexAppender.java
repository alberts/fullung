package net.lunglet.features.mfcc;

public final class IndexAppender {
    public Features apply(final Features features) {
        float[][] values = features.getValues();
        float[][] newValues = new float[values.length][];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                continue;
            }
            newValues[i] = new float[values[i].length + 1];
            System.arraycopy(values[i], 0, newValues[i], 0, values[i].length);
            // set last dimension to index of feature vector
            newValues[i][values[i].length] = i;
        }
        return features.replaceValues(newValues);
    }
}
