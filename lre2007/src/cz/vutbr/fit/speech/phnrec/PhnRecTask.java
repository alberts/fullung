package cz.vutbr.fit.speech.phnrec;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;

public final class PhnRecTask extends GridTaskAdapter<PhnRecJob> {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();

    @Override
    public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final PhnRecJob job)
            throws GridException {
        if (subgrid == null || subgrid.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<GridJob, GridNode> map = new HashMap<GridJob, GridNode>(1);
        map.put(job, subgrid.get(rng.nextInt(subgrid.size())));
        return map;
    }

    @Override
    public Object reduce(final List<GridJobResult> results) throws GridException {
        for (GridJobResult result : results) {
            if (result.getException() != null) {
                System.out.println("job failed:" + result.getException());
                continue;
            }
            Object[] actualResult = (Object[]) result.getData();
            String filename = (String) actualResult[0];
            int channel = (Integer) actualResult[1];
            byte[] buf = (byte[]) actualResult[2];
            String outputFile = filename + "_" + channel + ".phnrec.zip";
            if (buf != null) {
                System.out.println("writing " + outputFile);
                try {
                    OutputStream out = new FileOutputStream(outputFile);
                    out.write(buf);
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
