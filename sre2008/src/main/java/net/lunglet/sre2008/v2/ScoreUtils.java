package net.lunglet.sre2008.v2;

public final class ScoreUtils {
    public static double[] getParams(final float[] scores) {
        double n = 0.0;
        double mean = 0.0;
        double s = 0.0;
        for (int i = 0; i < scores.length; i++) {
            n += 1.0;
            double x = scores[i];
            double delta = x - mean;
            mean += delta / n;
            // s is updated using the new value of mean
            s += delta * (x - mean);
        }
        double stddev = Math.sqrt(s / n);
        return new double[]{mean, stddev};
    }
}
