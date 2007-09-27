package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.lunglet.gridgain.GridTaskManager;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class PhnRecGrid {
    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(false);
        topSpi.setRemoteNodes(true);
        return topSpi;
    }

    static final class PhnRecJobParameters {
        String filename;

        int channel;

        boolean isDone() {
            return new File(filename + "_" + channel + ".phnrec.zip").exists();
        }
    }

    private static List<PhnRecJobParameters> createJobParameters() throws IOException, UnsupportedAudioFileException {
        List<PhnRecJobParameters> jobParamsList = new ArrayList<PhnRecJobParameters>();
        String path = "G:/temp";
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        for (File inputFile : FileUtils.listFiles(path, filter, true)) {
            System.out.println("processing " + inputFile.getCanonicalPath());
            AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
            for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                PhnRecJobParameters jobParams = new PhnRecJobParameters();
                jobParams.filename = inputFile.getCanonicalPath();
                jobParams.channel = channel;
                if (!jobParams.isDone()) {
                    System.out.println("adding work unit for channel " + channel);
                    jobParamsList.add(jobParams);
                }
            }
        }
        return jobParamsList;
    }

    public static void main(final String[] args) throws Exception {
        final List<PhnRecJobParameters> jobParamsList = createJobParameters();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            GridConfigurationAdapter cfg = new GridConfigurationAdapter();
            cfg.setTopologySpi(createTopologySpi());
            cfg.setExecutorService(executorService);
            final Grid grid = GridFactory.start(cfg);
            int maximumJobs = 50;
            GridTaskManager<PhnRecJob> taskManager = null;
            taskManager = new GridTaskManager<PhnRecJob>(grid, PhnRecTask.class, maximumJobs);
            taskManager.execute(new PhnRecTaskFactory(jobParamsList));
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
