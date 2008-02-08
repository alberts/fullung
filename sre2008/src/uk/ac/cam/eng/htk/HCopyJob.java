package uk.ac.cam.eng.htk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.ProcessManager;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

public class HCopyJob implements GridJob {
    private static final long serialVersionUID = 1L;

    private static final ProcessManager HCOPY;

    static {
        String config =  "/uk/ac/cam/eng/htk/config.mfcc1";
        File tmpdir = new File("C:\\temp");
        try {
            HCOPY = new ProcessManager(new String[]{"C:\\temp\\HCopy.exe", config}, tmpdir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final String filename;

    private final int channel;

    private final byte[] buf;

    public HCopyJob(final String filename, final int channel) {
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File workingDir = HCOPY.getWorkingDirectory();
        // TODO write buf using HTKOutputStream to working dir
        // HCopy.exe -D -A -V -C config.mfcc src tgt
        // HCopy.exe -D -A -V -C config.plp src tgt
        return new Object[]{filename, channel, baos.toByteArray()};
    }
}
