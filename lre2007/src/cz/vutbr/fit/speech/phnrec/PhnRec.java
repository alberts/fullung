package cz.vutbr.fit.speech.phnrec;

import cz.vutbr.fit.speech.phnrec.PhnRecSystem.PhnRecSystemId;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
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

public final class PhnRec {
    private static final Log LOG = LogFactory.getLog(PhnRec.class);

    private static final PhnRecSystem[] PHNREC_SYSTEMS;

    static {
        try {
            PHNREC_SYSTEMS = new PhnRecSystem[]{new PhnRecSystem(PhnRecSystemId.PHN_CZ_SPDAT_LCRC_N1500),
                    new PhnRecSystem(PhnRecSystemId.PHN_HU_SPDAT_LCRC_N1500),
                    new PhnRecSystem(PhnRecSystemId.PHN_RU_SPDAT_LCRC_N1500)};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        File inputDirectory = new File("G:/lid07e1/data");
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        File[] inputFiles = FileUtils.listFiles(inputDirectory, filter, true);
        for (File inputFile : inputFiles) {
            processFile(inputFile);
        }
    }

    private static void processFile(final File inputFile) throws IOException, UnsupportedAudioFileException {
        LOG.info("processing " + inputFile.getCanonicalPath());
        AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
        boolean outputDone = false;
        for (int i = 0; i < format.getFormat().getChannels(); i++) {
            File outputFile = new File(inputFile.getCanonicalFile() + "_" + i + ".phnrec.zip");
            if (outputFile.exists()) {
                outputDone = true;
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
            // TODO can use a ByteArrayOutputStream instead
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempOutputFile));
            out.setLevel(9);
            for (PhnRecSystem system : PHNREC_SYSTEMS) {
                LOG.info("processing channel " + i + " with system " + system);
                system.processChannel(channelsData[i], out);
            }
            out.close();
            LOG.info("moving output file to " + outputFile.getCanonicalPath());
            outputFile.delete();
            if (!tempOutputFile.renameTo(outputFile)) {
                throw new RuntimeException();
            }
            LOG.info("moved output file");
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
