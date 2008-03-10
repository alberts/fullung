package net.lunglet.sre2008.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import net.lunglet.htk.HTKInputStream;
import net.lunglet.util.AssertUtils;

public final class CheckMFCC {
    public static void main(final String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        while (line != null && line.trim().length() > 0) {
            File file = new File(line);
            System.out.println("Checking " + file);
            HTKInputStream in = new HTKInputStream(file);
            float[][] mfcc = in.readMFCC();
            in.close();
            AssertUtils.assertTrue(mfcc.length > 0);
            for (int i = 0; i < mfcc.length; i++) {
                AssertUtils.assertEquals(38, mfcc[i].length);
                for (int j = 0; j < mfcc[i].length; j++) {
                    float v = mfcc[i][j];
                    AssertUtils.assertFalse(Float.isInfinite(v));
                    AssertUtils.assertFalse(Float.isNaN(v));
                    if (v < -3.0f) {
                        throw new RuntimeException("value is too small: " + v);
                    }
                    if (v > 3.0f) {
                        throw new RuntimeException("value is too big: " + v);
                    }
                }
            }
            line = reader.readLine();
        }
        reader.close();
    }
}
