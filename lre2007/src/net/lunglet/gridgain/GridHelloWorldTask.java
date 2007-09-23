package net.lunglet.gridgain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskSplitAdapter;
import org.gridgain.grid.resources.GridInstanceResource;

public final class GridHelloWorldTask extends GridTaskSplitAdapter<String> {
    private static final long serialVersionUID = 1L;

    @Override
    public Collection<? extends GridJob> split(final int gridSize, final String arg) throws GridException {
        List<String> words = Arrays.asList((arg).split(" "));
        List<GridJob> jobs = new ArrayList<GridJob>(words.size());
        for (String word : words) {
            jobs.add(new GridJobAdapter<String>(word) {
                private static final long serialVersionUID = 1L;

                private Grid grid = null;

                public Serializable execute() throws GridException {
                    return null;
                }

                @GridInstanceResource
                public void setGrid(final Grid grid) {
                    this.grid = grid;
                }
            });
        }

        return jobs;
    }

    public Serializable reduce(List<GridJobResult> results) throws GridException {
        return null;
    }
}
