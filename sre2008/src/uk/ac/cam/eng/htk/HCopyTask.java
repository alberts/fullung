package uk.ac.cam.eng.htk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;

public final class HCopyTask extends GridTaskAdapter<HCopyJob> {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();

    @Override
    public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final HCopyJob job)
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
            System.out.println("got result from " + result.getNode().getPhysicalAddress());
            Object[] actualResult = (Object[]) result.getData();
            String filename = (String) actualResult[0];
            int channel = (Integer) actualResult[1];
            byte[] buf = (byte[]) actualResult[2];

            // TODO get filename from somewhere
//            String outputFile = filename + "." + channel + ".phnrec.zip";
//            if (buf != null) {
//                System.out.println("writing " + outputFile);
//                try {
//                    OutputStream out = new FileOutputStream(outputFile);
//                    out.write(buf);
//                    out.close();
//                } catch (IOException e) {
//                    throw new GridException(null, e);
//                }
//            } else {
//                System.out.println("result for " + outputFile + " contained no data");
//            }

        }
        return null;
    }
}
