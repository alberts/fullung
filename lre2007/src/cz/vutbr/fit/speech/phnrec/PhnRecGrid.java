package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.sound.util.SoundUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class PhnRecGrid {
    static final Log LOG = LogFactory.getLog(PhnRecGrid.class);

    public static final class PhnRecWorkUnit implements Serializable {
        private static final long serialVersionUID = 1L;

        String file;

        int channel;

        byte[] buf;

        public PhnRecWorkUnit(final String file, final int channel, final byte[] buf) {
            this.file = file;
            this.channel = channel;
            this.buf = buf;
        }

        public PhnRecWorkUnit(final File file, final int channel) {
            try {
                this.file = file.getCanonicalPath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.channel = channel;
        }

        public boolean isDone() {
            return new File(file + "_" + channel + ".phnrec.zip").exists();
        }
    }

    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(false);
        topSpi.setRemoteNodes(true);
        return topSpi;
    }

    private static List<GridTaskFuture> executeTasks(final Grid grid) {
        List<GridTaskFuture> futures = new ArrayList<GridTaskFuture>();
        int count = 0;
        for (File inputFile : FileUtils.listFiles("G:/MIT/data", new FilenameSuffixFilter(".sph", true), true)) {
            try {
                LOG.info("processing " + inputFile.getCanonicalPath());
                AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
                for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                    PhnRecWorkUnit workunit = new PhnRecWorkUnit(inputFile, channel);
                     if (!workunit.isDone()) {
                         LOG.info("adding work unit for channel " + channel);
                         workunit.buf = SoundUtils.readChannel(inputFile, channel);
                         GridTaskFuture future = grid.execute(PhnRecTask.class.getName(), workunit);
                         System.out.println(future);
                         futures.add(future);
                         count++;
                     }
                }
                if (count > 500) {
                    break;
                }
            } catch (IOException e) {
                LOG.error("IOException while processing", e);
//                throw new RuntimeException(e);
            } catch (UnsupportedAudioFileException e) {
                LOG.error("UnsupportedAudioFileException while processing", e);
//                throw new RuntimeException(e);
            }
        }
        return futures;
    }

    public static void main(final String[] args) throws Exception {
        // TODO use a gridtasklistener to queue more tasks after the initial batch
        try {
            GridConfigurationAdapter cfg = new GridConfigurationAdapter();
            cfg.setTopologySpi(createTopologySpi());
            final Grid grid = GridFactory.start(cfg);
            while (true) {
                List<GridTaskFuture> futures = executeTasks(grid);
                System.out.println("got " + futures.size() + " futures");
                for (GridTaskFuture future : futures) {
                    future.get();
                }
            }
        } finally {
            GridFactory.stop(true);
        }
    }
}
