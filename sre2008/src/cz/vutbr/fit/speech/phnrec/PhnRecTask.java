package cz.vutbr.fit.speech.phnrec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskSplitAdapter;
import org.gridgain.grid.logger.GridLogger;
import org.gridgain.grid.resources.GridLoggerResource;

public final class PhnRecTask extends GridTaskSplitAdapter<PhnRecJob, Void> {
    private static final long serialVersionUID = 1L;
    
    @GridLoggerResource
    private GridLogger log = null;

    @Override
    public Void reduce(final List<GridJobResult> results) throws GridException {
        return null;
    }

    @Override
    protected Collection<PhnRecJob> split(int gridSize, PhnRecJob job) throws GridException {
        List<PhnRecJob> jobs = new ArrayList<PhnRecJob>();
        jobs.add(job);
        log.info("Mapped job for " + job);
        return jobs;
    }
}
