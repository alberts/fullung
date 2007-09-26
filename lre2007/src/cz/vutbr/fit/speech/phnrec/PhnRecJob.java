package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.Serializable;

import net.lunglet.sound.util.SoundUtils;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

public class PhnRecJob implements GridJob {
    private static final long serialVersionUID = 1L;
    
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
//        PhnRecWorkUnit workunit = getArgument();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        System.out.println("WORKING!");
//        ZipOutputStream out = new ZipOutputStream(baos);
//        out.setLevel(9);
//        try {
//            for (PhnRecSystem system : PhnRec.PHNREC_SYSTEMS) {
//                PhnRec.processChannel(workunit.buf, system, out);
//            }
//            out.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        String outputFile = workunit.file + "_" + workunit.channel + ".phnrec.zip";
//        PhnRecWorkUnit result = new PhnRecWorkUnit(outputFile, 0, baos.toByteArray());
        return new Object[]{filename, channel, null};
    }
}
