package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.util.AssertUtils;

// TODO maybe gaussianize each delta and delta-delta block on
// its own, but not with a padded gaussianization -- more windows
// to calculate when working with short segments, but then all
// features have the same range

public final class PhnRecMFCCBuilder {
    private static final boolean DEBUG = false;

    /// Threshold to discard blocks that contain too few feature vectors
    private static final int MIN_BLOCK_SIZE = 20;

    /// Threshold to discard blocks that contain too few phonemes
    private static final int MIN_PHONEMES_PER_BLOCK = 10;

    // use a window size of 302 to work around a bug in GaussWarp
    private static final int WINDOW_SIZE = 302;

    private static void convertFile(final PhnRecMFCCBuilder mfccBuilder, final String name)
            throws UnsupportedAudioFileException, IOException {
        File sphFile = new File(name);
        System.err.println("Reading " + sphFile);
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(sphFile);
        int channels = aff.getFormat().getChannels();
        ArrayList<MasterLabelFile> mlfs = new ArrayList<MasterLabelFile>();
        for (int i = 0; i < channels; i++) {
            File mlfFile = new File(sphFile.getAbsolutePath() + "." + i + ".mlf");
            System.err.println("Reading " + mlfFile);
            mlfs.add(new MasterLabelFile(mlfFile));
        }
        FeatureSet[] features = mfccBuilder.apply(sphFile, mlfs.toArray(new MasterLabelFile[0]));
        for (int i = 0; i < channels; i++) {
            File mfccFile = new File(sphFile.getAbsolutePath() + "." + i + ".mfc.gz");
            System.err.println("Writing " + mfccFile);
            MFCCBuilder.writeMFCC(mfccFile, features[i]);
        }
    }

    private static List<FeatureBlock> createBlocks(final FeatureSet currentChannel, final MasterLabelFile mlf) {
        List<FeatureBlock> blocks = new ArrayList<FeatureBlock>();
        final double framePeriod = currentChannel.getFramePeriodHTK() / 1.0e7;
        final double frameLength = currentChannel.getFrameLengthHTK() / 1.0e7;
        float[][] mfcc = currentChannel.getValues();
        ArrayList<float[]> blockValues = new ArrayList<float[]>();
        int beginIndex = -1;
        for (int i = 0; i < mfcc.length; i++) {
            double start = framePeriod * i;
            double end = start + frameLength;
            if (!mlf.containsTimestamp(start) || !mlf.containsTimestamp(end)) {
                break;
            }
            if (!mlf.isOnlySpeech(start, end)) {
                if (blockValues.size() >= MIN_BLOCK_SIZE) {
                    blocks.add(new FeatureBlock(beginIndex, i, blockValues.toArray(new float[0][])));
                }
                blockValues.clear();
                beginIndex = -1;
                continue;
            }
            if (beginIndex < 0) {
                // start of new block
                beginIndex = i;
            }
            blockValues.add(mfcc[i]);
        }
        if (blockValues.size() >= MIN_BLOCK_SIZE) {
            blocks.add(new FeatureBlock(beginIndex, mfcc.length, blockValues.toArray(new float[0][])));
        }
        return blocks;
    }

    private static void crossChannelSquelch(final List<FeatureBlock> blocks, final double maxBlockEnergydB,
            final List<FeatureSet> otherChannels) {
        List<FeatureBlock> badBlocks = new ArrayList<FeatureBlock>();
        for (FeatureBlock block : blocks) {
            for (FeatureSet features : otherChannels) {
                double otherBlockEnergy = block.getMeanEnergydB(features);
                if (otherBlockEnergy > maxBlockEnergydB - 3.0) {
                    badBlocks.add(block);
                    break;
                }
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

    public static void main(final String[] args) throws IOException {
        PhnRecMFCCBuilder mfccBuilder = new PhnRecMFCCBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        while (line != null && line.trim().length() > 0) {
            try {
                String name = line.trim();
                convertFile(mfccBuilder, name);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            line = reader.readLine();
        }
        reader.close();
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

    private static void removeNoiseBlocks(final List<FeatureBlock> blocks, final MasterLabelFile mlf) {
        List<FeatureBlock> badBlocks = new ArrayList<FeatureBlock>();
        for (FeatureBlock block : blocks) {
            int validPhonemeCount = mlf.getValidPhonemeCount(block.getBeginTime(), block.getEndTime());
            // block must contain some valid phonemes to make it here
            AssertUtils.assertTrue(validPhonemeCount > 0);
            if (validPhonemeCount < MIN_PHONEMES_PER_BLOCK) {
                badBlocks.add(block);
            }
        }
        blocks.removeAll(badBlocks);
    }

    private static void removeSilenceBlocks(final List<FeatureBlock> blocks, final double maxBlockEnergydB) {
        List<FeatureBlock> badBlocks = new ArrayList<FeatureBlock>();
        for (FeatureBlock block : blocks) {
            if (maxBlockEnergydB - block.getMeanEnergydB() > 30.0) {
                badBlocks.add(block);
            }
        }
        blocks.removeAll(badBlocks);
    }

    private final HTKMFCCBuilder htkmfcc;

    public PhnRecMFCCBuilder() {
        this.htkmfcc = new HTKMFCCBuilder();
    }

    public FeatureSet[] apply(final FeatureSet[] channels, final MasterLabelFile[] mlfs) {
        if (channels.length != mlfs.length) {
            throw new IllegalArgumentException();
        }
        List<List<FeatureBlock>> validBlocksList = new ArrayList<List<FeatureBlock>>();
        for (int channelIndex = 0; channelIndex < channels.length; channelIndex++) {
            List<FeatureSet> otherChannels = new ArrayList<FeatureSet>();
            FeatureSet currentChannel = null;
            for (int otherChannelIndex = 0; otherChannelIndex < channels.length; otherChannelIndex++) {
                if (channelIndex == otherChannelIndex) {
                    currentChannel = channels[channelIndex];
                } else {
                    otherChannels.add(channels[channelIndex]);
                }
            }
            MasterLabelFile mlf = mlfs[channelIndex];
            List<FeatureBlock> blocks = createBlocks(currentChannel, mlf);
            removeNoiseBlocks(blocks, mlf);
            double maxBlockEnergydB = getMaximumBlockEnergydB(blocks);
            removeSilenceBlocks(blocks, maxBlockEnergydB);
            crossChannelSquelch(blocks, maxBlockEnergydB, otherChannels);
            validBlocksList.add(blocks);
        }

        // modify blocks afterwards, so that cross-channel checks work
        FeatureSet[] newFeatures = new FeatureSet[channels.length];
        for (int channelIndex = 0; channelIndex < channels.length; channelIndex++) {
            List<FeatureBlock> blocks = validBlocksList.get(channelIndex);
            if (false) {
                for (FeatureBlock block : blocks) {
                    System.out.println(block.getMeanEnergydB() + " " + block.getLength() + " " + block.getBeginIndex()
                            * 10.0e-3 + " -> " + block.getEndIndex() * 10.0e-3);
                }
            }
            // gaussianize valid blocks in-place
            gaussianize(mergeBlockValues(blocks));
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
            }
            if (DEBUG) {
                for (FeatureBlock block : blocks) {
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
}
