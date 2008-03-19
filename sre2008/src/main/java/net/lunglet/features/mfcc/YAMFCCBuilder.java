package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import cz.vutbr.fit.speech.phnrec.MasterLabel;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFWriter;
import net.lunglet.util.AssertUtils;
import net.lunglet.util.CommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YAMFCCBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(YAMFCCBuilder.class);

    private static final int MIN_BLOCK_SIZE = 20;

    // use a window size of 302 to work around a bug in GaussWarp
    private static final int WINDOW_SIZE = 302;

    private static final boolean DEBUG = false;

    private static void crossChannelSquelch(final List<FeatureBlock> blocks, final double maxEnergydB,
            final FeatureSet otherChannel) {
        List<FeatureBlock> badBlocks = new ArrayList<FeatureBlock>();
        for (FeatureBlock block : blocks) {
            double otherEnergydB = block.getMeanEnergydB(otherChannel);
            if (otherEnergydB > maxEnergydB - 3.0) {
                badBlocks.add(block);
            }
        }
        blocks.removeAll(badBlocks);
    }

    private static float[][] delta(final float[][] features, final int beginIndex, final int endIndex) {
        AssertUtils.assertTrue(features.length >= 2);
        for (int i = 1; i < features.length; i++) {
            AssertUtils.assertTrue(features[0].length == features[i].length);
        }
        AssertUtils.assertTrue(beginIndex <= endIndex);
        AssertUtils.assertTrue(beginIndex >= 0);
        AssertUtils.assertTrue(endIndex <= features[0].length);
        float[][] deltas = new float[features.length][];
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = new float[endIndex - beginIndex];
        }
        for (int i = 0; i < deltas.length; i++) {
            for (int j = 1; j <= 2; j++) {
                // calculate index of future sample, replicating last vector if
                // needed
                int p = Math.min(i + j, deltas.length - 1);
                // calculate index of past sample, replicating first vector if
                // needed
                int m = Math.max(i - j, 0);
                for (int index = beginIndex; index < endIndex; index++) {
                    float cp = features[p][index];
                    float cm = features[m][index];
                    deltas[i][index - beginIndex] += j * (cp - cm);
                }
            }
            for (int index = beginIndex; index < endIndex; index++) {
                deltas[i][index - beginIndex] /= 2 * (1 * 1 + 2 * 2);
            }
        }
        return deltas;
    }

    private static void gaussianize(final float[][] values) {
        ArrayList<float[]> validValues = new ArrayList<float[]>();
        for (float[] v : values) {
            validValues.add(v);
        }
        AssertUtils.assertTrue(validValues.size() > 0);
        JMatrix mat = new JMatrix(validValues.toArray(new float[0][]));
        mat = mat.transpose();
        // pad up to expected window size to make gaussianization work
        if (validValues.size() < WINDOW_SIZE) {
            JVector mean = mat.meanOfColumns();
            JVector stddev = mat.columnScatter(mean).diagonal();
            stddev.scal(1.0 / mat.noColumns());
            stddev.sqrt();
            float[] xp = mean.plus(stddev).transpose().toFloatArray()[0];
            float[] xm = mean.minus(stddev).transpose().toFloatArray()[0];
            int requiredElements = WINDOW_SIZE - validValues.size();
            if (requiredElements % 2 == 0) {
                for (int i = 0; i < requiredElements / 2; i++) {
                    validValues.add(xp);
                    validValues.add(xm);
                }
            } else {
                for (int i = 0; i < requiredElements / 2; i++) {
                    validValues.add(xp);
                    validValues.add(xm);
                }
                validValues.add(mean.transpose().toFloatArray()[0]);
            }
            mat = new JMatrix(validValues.toArray(new float[0][]));
            mat = mat.transpose();
        }
        AssertUtils.assertTrue(mat.noColumns() >= WINDOW_SIZE);
        GaussWarp.warp(mat);
        mat = mat.transpose();
        float[][] warpedValues = mat.toFloatArray();
        for (int i = 0; i < values.length; i++) {
            AssertUtils.assertEquals(warpedValues[i].length, values[i].length);
            System.arraycopy(warpedValues[i], 0, values[i], 0, values[i].length);
        }
    }

    private static double getMaximumBlockEnergydB(final Collection<FeatureBlock> blocks) {
        double maxBlockEnergydB = Double.NEGATIVE_INFINITY;
        for (FeatureBlock block : blocks) {
            double blockEnergydB = block.getMeanEnergydB();
            if (blockEnergydB > maxBlockEnergydB) {
                maxBlockEnergydB = blockEnergydB;
            }
        }
        return maxBlockEnergydB;
    }

    private static List<FeatureBlock> getPhonemeBlocks(final FeatureSet channel, final MasterLabelFile mlf) {
        final double framePeriod = channel.getFramePeriodHTK() / 1.0e7;
        float[][] mfcc = channel.getValues();
        List<FeatureBlock> phonemeBlocks = new ArrayList<FeatureBlock>();
        for (MasterLabel label : mlf) {
            if (!label.isValid() && label.getDuration() >= 20.0e-3) {
                continue;
            }
            int fromIndex = (int) (label.getStartTime() / framePeriod);
            int toIndex = (int) (label.getEndTime() / framePeriod);
            float[][] mfccPart = new float[toIndex - fromIndex][];
            System.arraycopy(mfcc, fromIndex, mfccPart, 0, mfccPart.length);
            FeatureBlock phonemeBlock = new FeatureBlock(fromIndex, toIndex, mfccPart);
            phonemeBlocks.add(phonemeBlock);
        }
        return phonemeBlocks;
    }

    private static float[][] mergeBlockValues(final Collection<FeatureBlock> blocks) {
        ArrayList<float[]> valuesList = new ArrayList<float[]>();
        for (FeatureBlock block : blocks) {
            for (float[] v : block.getValues()) {
                valuesList.add(v);
            }
        }
        return valuesList.toArray(new float[0][]);
    }

    private static List<FeatureBlock> mergePhonemeBlocks(final List<FeatureBlock> phonemeBlocks) {
        List<FeatureBlock> mergedBlocks = new ArrayList<FeatureBlock>();
        int fromIndex = -1;
        int toIndex = -1;
        int phonemeCount = 0;
        ArrayList<float[]> valuesList = new ArrayList<float[]>();
        for (FeatureBlock block : phonemeBlocks) {
            if (toIndex != block.getFromIndex()) {
                if (valuesList.size() > 0) {
                    float[][] values = valuesList.toArray(new float[0][]);
                    FeatureBlock mergedBlock = new FeatureBlock(fromIndex, toIndex, values);
                    if (mergedBlock.getLength() >= MIN_BLOCK_SIZE) {
                        mergedBlocks.add(mergedBlock);
                    }
                    valuesList.clear();
                }
                // new block starts
                fromIndex = block.getFromIndex();
                toIndex = block.getToIndex();
                phonemeCount = 1;
            } else {
                // block continues
                toIndex = block.getToIndex();
                phonemeCount++;
            }
            for (float[] v : block.getValues()) {
                valuesList.add(v);
            }
        }
        return mergedBlocks;
    }

    private static void removeSilenceBlocks(final List<FeatureBlock> blocks, final double maxEnergydB) {
        List<FeatureBlock> badBlocks = new ArrayList<FeatureBlock>();
        for (FeatureBlock block : blocks) {
            if (maxEnergydB - block.getMeanEnergydB() > 30.0) {
                badBlocks.add(block);
            }
        }
        blocks.removeAll(badBlocks);
    }

    private final HTKMFCCBuilder htkmfcc;

    public YAMFCCBuilder() {
        this.htkmfcc = new HTKMFCCBuilder();
    }

    public FeatureSet[] apply(final FeatureSet[] channels, final MasterLabelFile[] mlfs) {
        if (channels.length == 0 || channels.length > 2 || channels.length != mlfs.length) {
            throw new IllegalArgumentException();
        }
        List<List<FeatureBlock>> validBlocksList = new ArrayList<List<FeatureBlock>>();
        for (int channelIndex = 0; channelIndex < channels.length; channelIndex++) {
            MasterLabelFile mlf = mlfs[channelIndex];
            List<FeatureBlock> phonemeBlocks = getPhonemeBlocks(channels[channelIndex], mlf);
            double maxEnergydB = getMaximumBlockEnergydB(phonemeBlocks);
            removeSilenceBlocks(phonemeBlocks, maxEnergydB);
            if (channels.length > 1) {
                FeatureSet otherChannel = channels[channelIndex == 0 ? 1 : 0];
                crossChannelSquelch(phonemeBlocks, maxEnergydB, otherChannel);
            }
            List<FeatureBlock> mergedBlocks = mergePhonemeBlocks(phonemeBlocks);
            validBlocksList.add(mergedBlocks);
        }
        FeatureSet[] newFeatures = new FeatureSet[channels.length];
        for (int channelIndex = 0; channelIndex < channels.length; channelIndex++) {
            List<FeatureBlock> blocks = validBlocksList.get(channelIndex);
            float[][] blockValues = mergeBlockValues(blocks);
            if (blockValues.length == 0) {
                // skip channel that had no valid data
                continue;
            }
            // gaussianize valid blocks in-place
            gaussianize(blockValues);
            // append deltas and delta-deltas
            for (FeatureBlock block : blocks) {
                float[][] delta = delta(block.getValues(), 0, 13);
                float[][] deltaDelta = delta(delta, 0, 13);
                float[][] values = block.getValues();
                AssertUtils.assertEquals(values.length, delta.length);
                AssertUtils.assertEquals(values.length, deltaDelta.length);
                for (int i = 0; i < values.length; i++) {
                    float[] v = values[i];
                    float[] d = delta[i];
                    float[] dd = deltaDelta[i];
                    float[] vddd = new float[v.length - 1 + d.length + dd.length];
                    // exclude log energy
                    System.arraycopy(v, 0, vddd, 0, v.length - 1);
                    // append deltas
                    System.arraycopy(d, 0, vddd, v.length - 1, d.length);
                    // append delta-deltas
                    System.arraycopy(dd, 0, vddd, v.length - 1 + d.length, dd.length);
                    // replace value in block
                    values[i] = vddd;
                }
                if (DEBUG) {
                    block.appendIndexes();
                }
            }
            newFeatures[channelIndex] = channels[channelIndex].replaceValues(mergeBlockValues(blocks));
        }
        return newFeatures;
    }

    public FeatureSet[] apply(final File file, final MasterLabelFile[] mlfs) throws UnsupportedAudioFileException,
            IOException {
        return apply(new FileInputStream(file), mlfs);
    }

    public FeatureSet[] apply(final InputStream stream, final MasterLabelFile[] mlfs)
            throws UnsupportedAudioFileException, IOException {
        return apply(htkmfcc.apply(stream), mlfs);
    }

    private static FeatureSet[] convertFile(final YAMFCCBuilder mfccBuilder, final String name) {
        try {
            File sphFile = new File(name);
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(sphFile);
            int channels = aff.getFormat().getChannels();
            LOGGER.info("Read {} with {} channels", sphFile, channels);
            ArrayList<MasterLabelFile> mlfs = new ArrayList<MasterLabelFile>();
            for (int i = 0; i < channels; i++) {
                File mlfFile = new File(sphFile.getAbsolutePath() + "." + i + ".mlf");
                LOGGER.info("Reading {}", mlfFile);
                mlfs.add(new MasterLabelFile(mlfFile));
            }
            return mfccBuilder.apply(sphFile, mlfs.toArray(new MasterLabelFile[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkMFCC(final float[][] mfcc) {
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
    }

    private static class MFCCTask implements Callable<Void> {
        private final String filename;

        private final H5File h5file;

        private static final ThreadLocal<YAMFCCBuilder> MFCC_BUILDER = new ThreadLocal<YAMFCCBuilder>() {
            @Override
            protected YAMFCCBuilder initialValue() {
                return new YAMFCCBuilder();
            }
        };

        public MFCCTask(final String filename, final H5File h5file) {
            this.filename = filename;
            this.h5file = h5file;
        }

        @Override
        public Void call() throws Exception {
            HDFWriter writer = new HDFWriter(h5file);
            try {
                YAMFCCBuilder mfccBuilder = MFCC_BUILDER.get();
                FeatureSet[] features = convertFile(mfccBuilder, filename);
                String hdfName = new File(filename).getName().split("\\.")[0];
                synchronized (h5file) {
                    h5file.getRootGroup().createGroup(hdfName);
                }
                for (int i = 0; i < features.length; i++) {
                    if (features[i] == null) {
                        LOGGER.info("Skipping invalid channel " + i + " in " + filename);
                        continue;
                    }
                    float[][] values = features[i].getValues();
                    if (true) {
                        checkMFCC(values);
                    }
                    FloatDenseMatrix matrix = DenseFactory.floatMatrix(values, Order.ROW, Storage.DIRECT);
                    String fullHdfName = "/" + hdfName + "/" + i;
                    LOGGER.info("Writing to " + fullHdfName);
                    synchronized (h5file) {
                        writer.write(fullHdfName, matrix);
                    }
                }
            } catch (Throwable t) {
                LOGGER.error("MFCC extraction for " + filename + " failed", t);
                throw new Exception(t);
            }
            return null;
        }
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        List<String> filenames = CommandUtils.getInput(args, System.in, YAMFCCBuilder.class);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        H5File h5file = new H5File("mfcc.h5", H5File.H5F_ACC_TRUNC);
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        for (String filename : filenames) {
            Future<Void> future = executorService.submit(new MFCCTask(filename, h5file));
            futures.add(future);
        }
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                LOGGER.error("Execution failed", e);
                continue;
            }
        }
        h5file.close();
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }
}
