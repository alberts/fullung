package cz.vutbr.fit.speech.phnrec;

import cz.vutbr.fit.speech.phnrec.PhnRecGrid.PhnRecJobParameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.lunglet.gridgain.GridTaskFactory;

public final class PhnRecTaskFactory implements GridTaskFactory<PhnRecJob> {
    private List<PhnRecJobParameters> jobParamsList;

    public PhnRecTaskFactory(final Collection<? extends PhnRecJobParameters> jobParamsList) {
        this.jobParamsList = new ArrayList<PhnRecJobParameters>(jobParamsList);
    }

    @Override
    public Iterator<PhnRecJob> iterator() {
        final Iterator<PhnRecJobParameters> it = jobParamsList.iterator();
        return new Iterator<PhnRecJob>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public PhnRecJob next() {
                PhnRecJobParameters jobParams = it.next();
                if (jobParams == null) {
                    return null;
                }
                return new PhnRecJob(jobParams.filename, jobParams.channel);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
