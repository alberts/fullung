package cz.vutbr.fit.speech.phnrec;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskSplitAdapter;

import cz.vutbr.fit.speech.phnrec.PhnRecGrid.PhnRecWorkUnit;

public final class PhnRecTask extends GridTaskSplitAdapter<PhnRecWorkUnit> {
    private static final long serialVersionUID = 1L;

    @Override
    public Collection<? extends GridJob> split(final int gridSize, final PhnRecWorkUnit workunit)
            throws GridException {
        List<GridJob> jobs = new ArrayList<GridJob>();
        jobs.add(new GridJobAdapter<PhnRecWorkUnit>(workunit) {
            private static final long serialVersionUID = 1L;

            public Serializable execute() throws GridException {
                PhnRecWorkUnit workunit = getArgument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.out.println("WORKING!");
                ZipOutputStream out = new ZipOutputStream(baos);
                out.setLevel(9);
                try {
                    for (PhnRecSystem system : PhnRec.PHNREC_SYSTEMS) {
                        PhnRec.processChannel(workunit.buf, system, out);
                    }
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String outputFile = workunit.file + "_" + workunit.channel + ".phnrec.zip";
                PhnRecWorkUnit result = new PhnRecWorkUnit(outputFile, 0, baos.toByteArray());
                return result;
            }
        });
        return jobs;
    }

    public Serializable reduce(final List<GridJobResult> results) throws GridException {
        for (GridJobResult result : results) {
            if (result.getException() != null) {
                System.out.println("job failed: " + result.getException());
            }
            PhnRecWorkUnit actualResult = (PhnRecWorkUnit) result.getData();
            String outputFile = actualResult.file;
            if (actualResult.buf != null) {
                System.out.println("writing " + outputFile);
                try {
                    OutputStream out = new FileOutputStream(outputFile);
                    out.write(actualResult.buf);
                    out.close();
                } catch (IOException e) {
                    throw new GridException(null, e);
                }
            } else {
                System.out.println("result for " + outputFile + " contained no data");
            }
        }
        return null;
    }
}
