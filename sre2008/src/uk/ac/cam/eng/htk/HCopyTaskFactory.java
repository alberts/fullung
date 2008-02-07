package uk.ac.cam.eng.htk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.lunglet.gridgain.GridTaskFactory;
import uk.ac.cam.eng.htk.HCopyGrid.HCopyJobParameters;

public final class HCopyTaskFactory implements GridTaskFactory<HCopyJob> {
    private List<HCopyJobParameters> jobParamsList;

    public HCopyTaskFactory(final Collection<? extends HCopyJobParameters> jobParamsList) {
        this.jobParamsList = new ArrayList<HCopyJobParameters>(jobParamsList);
    }

    @Override
    public Iterator<HCopyJob> iterator() {
        final Iterator<HCopyJobParameters> it = jobParamsList.iterator();
        return new Iterator<HCopyJob>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public HCopyJob next() {
                HCopyJobParameters jobParams = it.next();
                if (jobParams == null) {
                    return null;
                }
                return new HCopyJob(jobParams.filename, jobParams.channel);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
