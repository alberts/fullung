package net.lunglet.lre.lre07;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import cz.vutbr.fit.speech.phnrec.MasterLabel;
import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

public final class CreateBigrams2 {
    public static void main(final String[] args) throws IOException {
        final DataType dtype = FloatType.IEEE_F32LE;
        final String inputDirectory = "F:/language/CallFriend";
        File[] files = FileUtils.listFiles(new File(inputDirectory), new FilenameSuffixFilter(".phnrec.zip"), true);
        Pattern pattern = Pattern.compile("(.*)\\.sph_(\\d+)", Pattern.CASE_INSENSITIVE);
        H5File hdf = new H5File("ngrams.h5", H5File.H5F_ACC_RDWR);
        Group rootGroup = hdf.getRootGroup();
        Group cf = rootGroup.createGroup("CallFriend");
        for (File file : files) {
            System.out.println(file);
            Matcher matcher = pattern.matcher(file.getName());
            if (!matcher.find()) {
                System.out.println("ignoring " + file);
                continue;
            }
            String name = matcher.group(1) + "_" + matcher.group(2);
            List<List<FloatDenseVector>> segments = readPhnRecZip(file);
            List<FloatDenseVector> allngrams = new ArrayList<FloatDenseVector>();
            for (List<FloatDenseVector> segment : segments) {
                FloatDenseMatrix posteriors = new FloatDenseMatrix(segment.get(0).rows(), segment.size());
                for (int j = 0; j < segment.size(); j++) {
                    posteriors.setColumn(j, segment.get(j));
                }
//                FloatDenseVector ngrams = PhonemeUtil.calculateMonoBigrams(posteriors, 1);
                FloatDenseVector ngrams = null;
                if (ngrams == null) {
                    continue;
                }
                allngrams.add(ngrams);
            }
            if (allngrams.size() == 0) {
                System.out.println("no ngrams for " + file);
                continue;
            }
            // write ngrams as rows in row-major order
            int columns = allngrams.get(0).length();
            DataSet ds = cf.createDataSet(name, dtype, allngrams.size(), columns);
            DataSpace memSpace = new DataSpace(columns);
            DataSpace fileSpace = ds.getSpace();
            for (int i = 0; i < allngrams.size(); i++) {
                FloatDenseVector x = allngrams.get(i);
                if (x.length() != columns) {
                    throw new AssertionError();
                }
                if (x.stride() != 1) {
                    throw new AssertionError();
                }
                long[] start = {i, 0};
                long[] count = {1, 1};
                long[] block = {1, columns};
                fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block); 
                ds.write(x.data(), dtype, memSpace, fileSpace);
            }
            fileSpace.close();
            memSpace.close();
            ds.close();
        }
        cf.close();
        rootGroup.close();
        hdf.close();
    }

    private static List<List<FloatDenseVector>> readPhnRecZip(final File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        PhnRecFeatures features = new PhnRecFeatures("cz", stream);
        stream.close();
        FloatDenseMatrix posteriors = features.getPosteriors();
        List<MasterLabel> labels = features.getLabels();
        List<List<FloatDenseVector>> segments = new ArrayList<List<FloatDenseVector>>();
        long duration = 0;
        List<FloatDenseVector> segment = new ArrayList<FloatDenseVector>();
        for (int i = 0, j = 0; i < labels.size(); i++) {
            MasterLabel label = labels.get(i);
            if (label.isValid()) {
                segment.add(posteriors.column(j++));
                // only take valid segments into account
                duration += label.getDuration();
            }
            if (duration >= 30 * 1e7) {
                segments.add(segment);
                duration = 0L;
                segment = new ArrayList<FloatDenseVector>();
            }
        }
        // TODO maybe add last segment if it is long enough
        return segments;
    }

    private CreateBigrams2() {
    }
}
