package net.lunglet.lre.lre07;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;

import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import cz.vutbr.fit.speech.phnrec.PhonemeUtil;

public final class CreateBigrams {
    private static FloatDenseVector calculateNGrams(final File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        PhnRecFeatures features = new PhnRecFeatures("cz", stream);
        stream.close();
        FloatDenseMatrix posteriors = features.getPosteriors();
        // this can happen with very noisy files
        if (posteriors.columns() < 2) {
            System.out.println("No posteriors for " + file);
            return null;
        }
        FloatDenseVector ngrams = PhonemeUtil.calculateNGrams(posteriors);
        return ngrams;
    }

    public static void main(final String[] args) throws IOException {
        Pattern pattern = Pattern.compile("(.*)\\.sph_(\\d+)", Pattern.CASE_INSENSITIVE);

        Map<String, String> groups = new HashMap<String, String>();
        groups.put("lid03e1", "F:/language/NIST/lid03e1/test/30");
        groups.put("lid05d1", "F:/language/NIST/lid05d1/test/30");
        groups.put("lid05e1", "F:/language/NIST/lid05e1/test/30");
        groups.put("lid96d1", "F:/language/NIST/lid96d1/test/30");
        groups.put("lid96e1", "F:/language/NIST/lid96e1/test/30");

        H5File hdf = new H5File("ngrams.h5");
        Group rootGroup = hdf.getRootGroup();
        for (Map.Entry<String, String> entry : groups.entrySet()) {
            Group group = rootGroup.createGroup(entry.getKey());
            Group group2 = group.createGroup("30");
            String directory = entry.getValue();
            File[] files = FileUtils.listFiles(new File(directory), new FilenameSuffixFilter(".phnrec.zip"), true);
            for (File file : files) {
                System.out.println(file);
                Matcher matcher = pattern.matcher(file.getName());
                if (!matcher.find()) {
                    System.out.println("skipping " + file);
                }
                String name = matcher.group(1) + "_" + matcher.group(2);
                FloatDenseVector ngrams = calculateNGrams(file);
                if (ngrams == null) {
                    continue;
                }
                DataType dtype = FloatType.IEEE_F32LE;
                DataSet dataset = group2.createDataSet(name, dtype, 1, ngrams.length());
                ByteBuffer buf = ByteBuffer.allocateDirect(4 * ngrams.length()).order(ByteOrder.nativeOrder());
                buf.asFloatBuffer().put(ngrams.toArray());
                dataset.write(buf, dtype);
                dataset.close();
            }
            group2.close();
            group.close();
        }
        rootGroup.close();
        hdf.close();
    }

    private CreateBigrams() {
    }
}
