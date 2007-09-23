package cz.vutbr.fit.speech.phnrec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.sound.sampled.RawAudioFileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.array4j.dense.FloatDenseMatrix;

public final class PhnRec {
    private static final Log LOG = LogFactory.getLog(PhnRec.class);

    public static final PhnRecSystem[] PHNREC_SYSTEMS = {new PhnRecSystem("PHN_CZ_SPDAT_LCRC_N1500", "cz"),
            new PhnRecSystem("PHN_HU_SPDAT_LCRC_N1500", "hu"), new PhnRecSystem("PHN_RU_SPDAT_LCRC_N1500", "ru")};

    private static final File TEMP_PCM_FILE;

    private static final File TEMP_POSTERIORS_FILE;

    private static final File TEMP_STRINGS_FILE;

    static {
        try {
            String phnRecTmpDir = System.getProperty("phnrec.tmpdir", null);
            File baseTempDir = phnRecTmpDir != null ? new File(phnRecTmpDir) : null;
            TEMP_PCM_FILE = File.createTempFile("pcm", ".snd", baseTempDir).getCanonicalFile();
            TEMP_PCM_FILE.deleteOnExit();
            TEMP_POSTERIORS_FILE = File.createTempFile("post", ".htk", baseTempDir).getCanonicalFile();
            TEMP_POSTERIORS_FILE.deleteOnExit();
            TEMP_STRINGS_FILE = File.createTempFile("mlf", ".txt", baseTempDir).getCanonicalFile();
            TEMP_STRINGS_FILE.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        File inputDirectory = new File("F:/test");
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        File[] inputFiles = FileUtils.listFiles(inputDirectory, filter, true);
        for (File inputFile : inputFiles) {
            processFile(inputFile);
        }
    }

    public static void processChannel(final byte[] channelData, final PhnRecSystem system, final ZipOutputStream out)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(TEMP_PCM_FILE);
        fos.write(channelData);
        fos.close();
        system.waveformToPosteriors(TEMP_PCM_FILE, TEMP_POSTERIORS_FILE);
        FloatDenseMatrix posteriors = system.readPosteriors(TEMP_POSTERIORS_FILE);
        LOG.info("posteriors size = [" + posteriors.rows() + ", " + posteriors.columns() + "]");
        system.posteriorsToStrings(TEMP_POSTERIORS_FILE, TEMP_STRINGS_FILE);
        List<MasterLabel> labels = system.readStrings(TEMP_STRINGS_FILE);
        PosteriorsConverter postConv = new PosteriorsConverter(posteriors, labels);
        out.putNextEntry(new ZipEntry(system.getShortName() + ".mlf"));
        postConv.writeMasterLabels(out);
        out.closeEntry();
        out.putNextEntry(new ZipEntry(system.getShortName() + ".post"));
        postConv.writePhonemePosteriors(out);
        out.closeEntry();
    }

    private static void processFile(final File inputFile) throws IOException, UnsupportedAudioFileException {
        LOG.info("processing " + inputFile.getCanonicalPath());
        AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
        boolean outputDone = true;
        for (int i = 0; i < format.getFormat().getChannels(); i++) {
            File outputFile = new File(inputFile.getCanonicalFile() + "_" + i + ".phnrec.zip");
            if (!outputFile.exists()) {
                outputDone = false;
                break;
            }
        }
        if (outputDone) {
            LOG.info("skipping " + inputFile.getCanonicalPath() + " entirely");
            return;
        }
        AudioInputStream sourceStream = AudioSystem.getAudioInputStream(inputFile);
        AudioInputStream targetStream = AudioSystem.getAudioInputStream(Encoding.PCM_SIGNED, sourceStream);
        byte[][] channelsData = splitChannels(targetStream);
        targetStream.close();
        for (int i = 0; i < channelsData.length; i++) {
            File outputFile = new File(inputFile.getCanonicalFile() + "_" + i + ".phnrec.zip");
            if (outputFile.isFile()) {
                LOG.info("skipping " + outputFile.getCanonicalPath());
                continue;
            }
            File tempOutputFile = File.createTempFile("phnrec", ".zip");
            tempOutputFile.deleteOnExit();
            LOG.info("Temporary output file = " + tempOutputFile.getCanonicalPath());
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempOutputFile));
            out.setLevel(9);
            for (PhnRecSystem system : PHNREC_SYSTEMS) {
                LOG.info("processing channel " + i + " with system " + system.getShortName());
                processChannel(channelsData[i], system, out);
            }
            out.close();
            if (!tempOutputFile.renameTo(outputFile)) {
                throw new RuntimeException();
            }
            LOG.info("moved output to file = " + outputFile.getCanonicalPath());
        }
    }

    private static byte[][] splitChannels(final AudioInputStream sourceStream) throws IOException {
        int channels = sourceStream.getFormat().getChannels();
        byte[][] channelsData = new byte[channels][];
        int sampleSizeInBits = sourceStream.getFormat().getSampleSizeInBits();
        if (sampleSizeInBits % 8 != 0) {
            throw new UnsupportedOperationException();
        }
        int sampleSizeInBytes = sampleSizeInBits >>> 3;
        if (sourceStream.getFrameLength() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException();
        }
        int frameLength = (int) sourceStream.getFrameLength();
        for (int i = 0; i < channels; i++) {
            channelsData[i] = new byte[sampleSizeInBytes * frameLength];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioSystem.write(sourceStream, RawAudioFileWriter.RAW, baos);
        byte[] samples = baos.toByteArray();
        final int expectedLength = channels * sampleSizeInBytes * frameLength;
        if (samples.length != expectedLength) {
            throw new RuntimeException("short read from audio stream");
        }
        for (int i = 0, sampleOffset = 0; i < frameLength; i++) {
            for (int j = 0; j < channels; j++) {
                for (int k = 0; k < sampleSizeInBytes; k++, sampleOffset++) {
                    int channelOffset = i * sampleSizeInBytes + k;
                    channelsData[j][channelOffset] = samples[sampleOffset];
                }
            }
        }
        return channelsData;
    }
}
