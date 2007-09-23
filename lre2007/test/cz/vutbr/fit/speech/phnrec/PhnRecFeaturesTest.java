package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;

public final class PhnRecFeaturesTest {
    @Test
    public void test() throws IOException {
//        File inputFile = new File("F:/language/NIST/lid96d1/test/30/02rz.sph_0.phnrec.zip");
        File inputFile = new File("F:/language/CallFriend/Vietnamese/CF_VIE/TRAIN/VI_5322.SPH_1.phnrec.zip");
        ArrayList<long[]> systemEvents = new ArrayList<long[]>();
        Map<String, Segment[]> systemSegments = new HashMap<String, Segment[]>();
        for (String system : new String[]{"cz", "hu", "ru"}) {
            InputStream stream = new FileInputStream(inputFile);
            PhnRecFeatures features = new PhnRecFeatures(system, stream);
            stream.close();
            for (MasterLabel label : features.getValidLabels()) {
//                System.out.println(label);
            }
            for (Segment segment : features.getValidSegments()) {
//                System.out.println(segment);
            }
            FloatDenseMatrix posteriors = features.getPosteriors();
            System.out.println(posteriors.column(0));
            System.out.println(posteriors.rows() + " " + posteriors.columns());
            FloatDenseVector ngrams = PhonemeUtil.calculateNGrams(posteriors);
            System.out.println(ngrams.length());

            Segment[] validSegments = features.getValidSegments().toArray(new Segment[0]);
            systemSegments.put(system, validSegments);
            long[] events = getEvents(validSegments);
            systemEvents.add(events);
        }

        long[] sortedEvents = sortEvents(systemEvents.toArray(new long[0][]));
        boolean[] votes = new boolean[sortedEvents.length];

        for (int i = 0; i < sortedEvents.length; i++) {
            long event = sortedEvents[i];
            votes[i] = true;
            for (Segment[] segments : systemSegments.values()) {
                // if any system voted no (i.e., segment containing the
                // timestamp was not valid according to all systems), discard
                // this part of the utterance
                if (!vote(event, segments)) {
                    votes[i] = false;
                    break;
                }
            }
        }

        List<Segment> validSegments = mergeEvents(sortedEvents, votes);
        for (Segment segment : validSegments) {
//            System.out.println(segment);
        }

        // TODO might want to discard segments shorter than a certain length
    }

    public List<Segment> mergeEvents(final long[] events, final boolean[] votes) {
        if (events.length != votes.length) {
            throw new IllegalArgumentException();
        }
        List<Segment> segments = new ArrayList<Segment>();
        long startTime = -1L;
        for (int i = 0; i < votes.length; i++) {
            if (votes[i] && startTime < 0L) {
                startTime = events[i];
            } else if (!votes[i] && startTime >= 0L) {
                segments.add(new Segment(startTime, events[i]));
                startTime = -1L;
            }
        }
        return segments;
    }

    private static boolean vote(final long event, final Segment[] segments) {
        for (Segment segment : segments) {
            if (segment.contains(event)) {
                return true;
            }
            // segments are sorted, so if the current segment starts after the
            // specified timestamp, all remaining segments may be
            // ignored
            if (segment.getStartTime() > event) {
                break;
            }
        }
        return false;
    }

    private static long[] getEvents(final Segment... segments) {
        long[] events = new long[2 * segments.length];
        for (int i = 0, j = 0; i < segments.length; i++, j += 2) {
            events[j] = segments[i].startTime;
            events[j + 1] = segments[i].endTime;
        }
        return events;
    }

    private static long[] sortEvents(final long[]... events) {
        int length = 0;
        for (int i = 0; i < events.length; i++) {
            length += events[i].length;
        }
        long[] sortedEvents = new long[length];
        for (int i = 0, j = 0; i < events.length; i++) {
            long[] e = events[i];
            System.arraycopy(e, 0, sortedEvents, j, e.length);
            j += e.length;
        }
        Arrays.sort(sortedEvents);
        return sortedEvents;
    }
}
