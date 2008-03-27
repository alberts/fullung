package net.lunglet.sre2008.svm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;

public final class SpeakerTrainTask extends GridTaskAdapter<SpeakerTrainJob, SpeakerTrainResult> {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();

    @Override
    public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final SpeakerTrainJob arg)
            throws GridException {
        Map<SpeakerTrainJob, GridNode> map = new HashMap<SpeakerTrainJob, GridNode>();
        map.put(arg, subgrid.get(rng.nextInt(subgrid.size())));
        return map;
    }

    @Override
    public SpeakerTrainResult reduce(List<GridJobResult> results) throws GridException {
        return results.get(0).getData();
    }
}
