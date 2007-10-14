package cz.vutbr.fit.speech.phnrec;

import cz.vutbr.fit.speech.phnrec.PhnRecSystem.PhnRecSystemId;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.ZipOutputStream;
import net.lunglet.sound.util.SoundUtils;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

public class PhnRecJob implements GridJob {
    private static final long serialVersionUID = 1L;

    // TODO this is probably going to leak
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(baos);
        out.setLevel(9);
        try {
            for (PhnRecSystem system : PHNREC_SYSTEMS) {
                system.processChannel(buf, out);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Object[]{filename, channel, baos.toByteArray()};
    }
}
