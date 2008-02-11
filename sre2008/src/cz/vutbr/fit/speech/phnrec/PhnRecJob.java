package cz.vutbr.fit.speech.phnrec;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.ProcessManager;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

public class PhnRecJob implements GridJob {
    private static final long serialVersionUID = 1L;

    private static final ProcessManager PHNREC;

    private static final File TEMP_PCM_FILE;

    private static final File TEMP_MLF_FILE;

    static {
        String executable = "/home/albert/opt/bin/phnrec";
        if (new File(executable).exists()) {
            String configZip = "/cz/vutbr/fit/speech/phnrec/PHN_CZ_SPDAT_LCRC_N1500.zip";
            File tmpdir = new File("/tmp");
            try {
                PHNREC = new ProcessManager(new String[]{executable, configZip}, tmpdir);
                File workingDir = PHNREC.getWorkingDirectory();
                TEMP_PCM_FILE = File.createTempFile("pcm", ".snd", workingDir);
                TEMP_PCM_FILE.deleteOnExit();
                TEMP_MLF_FILE = File.createTempFile("mlf", ".txt", workingDir);
                TEMP_MLF_FILE.deleteOnExit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            PHNREC = null;
            TEMP_PCM_FILE = null;
            TEMP_MLF_FILE = null;
        }
    }

    private final String filename;

    private final int channel;

    private final byte[] buf;

    public PhnRecJob(final String filename, final int channel) {
        this.filename = filename;
        this.channel = channel;
        this.buf = SoundUtils.readChannel(new File(filename), channel);
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Serializable execute() throws GridException {
        try {
            FileOutputStream fos = new FileOutputStream(TEMP_PCM_FILE);
            fos.write(buf);
            fos.close();
            List<String> arguments = new ArrayList<String>();
            arguments.add("-v");
            arguments.add("-c");
            arguments.add(PHNREC.getWorkingDirectory().getAbsolutePath());
            // source is single channel linear 16-bit PCM data
            arguments.add("-s");
            arguments.add("wf");
            arguments.add("-w");
            arguments.add("lin16");
            arguments.add("-i");
            arguments.add(TEMP_PCM_FILE.getAbsolutePath());
            // target is mlf text file
            arguments.add("-t");
            arguments.add("str");
            arguments.add("-o");
            arguments.add(TEMP_MLF_FILE.getAbsolutePath());
            PHNREC.run(arguments);
            DataInputStream stream = new DataInputStream(new FileInputStream(TEMP_MLF_FILE));
            byte[] buf = new byte[(int) TEMP_MLF_FILE.length()];
            stream.readFully(buf);
            stream.close();
            return new Object[]{filename, channel, buf};
        } catch (IOException e) {
            throw new GridException(null, e);
        }
    }
}
