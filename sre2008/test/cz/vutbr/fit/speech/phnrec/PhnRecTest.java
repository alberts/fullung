package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.ProcessManager;
import org.junit.Test;

// TODO scan through headers of all the data we're interested in
// TODO read channels on all of them
// TODO make worker threads to manage phnrec processes

public final class PhnRecTest {
    private static final double PHONEME_INSERTION_PENALTY = -3.8;
//    private static final double PHONEME_INSERTION_PENALTY = -5;

    @Test
    public void test() throws IOException {
        String sphereFilename = "C:\\temp\\data\\a.sph";
        int channel = 1;
        byte[] buf = SoundUtils.readChannel(new File(sphereFilename), channel);

        String exe = "C:\\temp\\phnrec.exe";
        String modelZip = "/cz/vutbr/fit/speech/phnrec/PHN_CZ_SPDAT_LCRC_N1500.zip";
        File tmpdir = new File("C:\\temp");
        ProcessManager phnrec = new ProcessManager(new String[]{exe, modelZip}, tmpdir);

        File inputFile = new File(phnrec.getWorkingDirectory(), "temp.wav");
        inputFile.deleteOnExit();
        OutputStream inputStream = new FileOutputStream(inputFile);
        inputStream.write(buf);
        inputStream.close();

        List<String> arguments = new ArrayList<String>();
        arguments.add("-v");
        arguments.add("-c");
        arguments.add(phnrec.getWorkingDirectory().getAbsolutePath());
        arguments.add("-i");
        arguments.add(inputFile.getAbsolutePath());

        arguments.add("-o");
        arguments.add(sphereFilename + "." + channel + ".mlf");

        arguments.add("-s");
        arguments.add("wf");
        arguments.add("-w");
        arguments.add("lin16");
        arguments.add("-t");
        arguments.add("str");

        // phoneme insertion penalty
//        arguments.add("-p");
//        arguments.add(Double.toString(PHONEME_INSERTION_PENALTY));

        System.out.println("processing channel " + channel + " from " + sphereFilename);
        phnrec.run(arguments);
    }
}
