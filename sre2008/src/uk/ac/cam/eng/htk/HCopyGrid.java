package uk.ac.cam.eng.htk;

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

public final class HCopyGrid {
    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(false);
        topSpi.setRemoteNodes(true);
        return topSpi;
    }

    static final class HCopyJobParameters {
        String filename;

        int channel;

        boolean isDone() {
            return new File(filename + "." + channel + ".mfc1").exists();
        }
    }

    private static List<HCopyJobParameters> createJobParameters() throws IOException, UnsupportedAudioFileException {
        List<HCopyJobParameters> jobParamsList = new ArrayList<HCopyJobParameters>();
        String path = "C:\\temp\\data";
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        for (File inputFile : FileUtils.listFiles(path, filter, true)) {
            System.out.println("processing " + inputFile.getCanonicalPath());
            AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
            for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                HCopyJobParameters jobParams = new HCopyJobParameters();
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
        final List<HCopyJobParameters> jobParamsList = createJobParameters();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            GridConfigurationAdapter cfg = new GridConfigurationAdapter();
            cfg.setTopologySpi(createTopologySpi());
            cfg.setExecutorService(executorService);
            final Grid grid = GridFactory.start(cfg);
            int maximumJobs = 50;
            GridTaskManager<HCopyJob> taskManager = null;
            taskManager = new GridTaskManager<HCopyJob>(grid, HCopyTask.class, maximumJobs);
            taskManager.execute(new HCopyTaskFactory(jobParamsList));
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
