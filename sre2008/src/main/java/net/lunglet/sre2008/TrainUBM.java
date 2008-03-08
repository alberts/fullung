package net.lunglet.sre2008;

import com.dvsoft.sv.toolbox.matrix.JVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import net.lunglet.htk.HTKDataType;
import net.lunglet.htk.HTKHeader;
import net.lunglet.htk.HTKInputStream;

public final class TrainUBM {
    private static class HTKVectorSequence implements JVectorSequence {
        private final int count;

        private final int dimension;

        private final File file;

        private float[][] mfcc;

        private int position;

        private WeakReference<float[][]> weakMfcc;

        public HTKVectorSequence(final File file) throws IOException {
            this.file = file;
            HTKInputStream in = getStream();
            HTKHeader header = in.readHeader();
            in.close();
            if (header.getDataType() != HTKDataType.MFCC) {
                throw new IOException();
            }
            this.dimension = header.getFrameSize() / 4;
            this.count = header.getFrames();
            this.mfcc = null;
            this.position = 0;
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public JVector getNextVector() {
            if (mfcc == null) {
                mfcc = weakMfcc != null ? weakMfcc.get() : null;
                if (mfcc == null) {
                    System.out.println("Reading data for " + file);
                    try {
                        HTKInputStream stream = getStream();
                        try {
                            this.mfcc = stream.readMFCC();
                        } finally {
                            stream.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Reusing weak reference for " + file);
                }
            }
            if (position >= mfcc.length) {
                return null;
            }
            return new JVector(mfcc[position++]);
        }

        private HTKInputStream getStream() throws IOException {
            final InputStream stream;
            if (file.getName().endsWith(".gz")) {
                stream = new GZIPInputStream(new FileInputStream(file), 16384);
            } else {
                stream = new FileInputStream(file);
            }
            return new HTKInputStream(stream);
        }

        @Override
        public int noVectors() {
            return count;
        }

        @Override
        public void reset() {
            // keep a weak reference
            if (mfcc != null) {
                this.weakMfcc = new WeakReference<float[][]>(mfcc);
            }
            this.mfcc = null;
            this.position = 0;
        }

        @Override
        public int skip(int noVectors) {
            throw new UnsupportedOperationException();
        }
    }

    public static VQ kMeans(JVectorSequence[] data, int log2K) throws InterruptedException, ExecutionException,
            IOException {
        int nThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        VQAccumulator acc = new VQAccumulator(log2K, data[0].getDimension(), 10);
        boolean reverse = false;
        int iter = 0;
        while (true) {
            VQStats stats = acc.newIteration();
            List<Future<VQStats>> futures = new ArrayList<Future<VQStats>>();
            if (reverse) {
                for (int i = data.length - 1; i >= 0; i--) {
                    futures.add(submit(executorService, data[i], stats.copy()));
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    futures.add(submit(executorService, data[i], stats.copy()));
                }
            }
            reverse = !reverse;
            for (Future<VQStats> future : futures) {
                acc.add(future.get());
            }
            futures.clear();
            boolean done = acc.update();

            RWFolder vqDir = new FileFolder(".");
            IOReference vqFile = vqDir.createIOReference("iter" + iter + ".vq");
            IO.saveObject(acc, vqFile);
            iter++;
            System.out.println("iteration saved: " + vqFile);

            if (done) {
                break;
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        return acc;
    }

    public static void main(final String[] args) throws IOException {
        List<String> filenames = getInput(args, System.in);
        ArrayList<JVectorSequence> data = new ArrayList<JVectorSequence>();
        for (String filename : filenames) {
            System.out.println("Reading " + filename);
            data.add(new HTKVectorSequence(new File(filename)));
        }
        if (data.size() == 0) {
            return;
        }
        try {
            int log2k = 11;
            VQ vq = kMeans(data.toArray(new JVectorSequence[0]), log2k);
            RWFolder vqDir = new FileFolder(".");
            IOReference vqFile = vqDir.createIOReference("ubm.vq");
            IO.saveObject(vq, vqFile);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getInput(final String[] args, final InputStream in) throws IOException {
        List<String> lines = new ArrayList<String>();
        final BufferedReader reader;
        if (args.length > 0) {
            if (args.length != 1 || !new File(args[0]).exists()) {
                throw new RuntimeException();
            }
            reader = new BufferedReader(new FileReader(new File(args[0])));
        } else {
            reader = new BufferedReader(new InputStreamReader(in));
        }
        try {
            String line = reader.readLine();
            while (line != null && line.trim().length() > 0) {
                lines.add(line.trim());
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return lines;
    }

    private static Future<VQStats> submit(final ExecutorService executorService, final JVectorSequence data,
            final VQStats stats) {
        Future<VQStats> future = executorService.submit(new Callable<VQStats>() {
            @Override
            public VQStats call() throws Exception {
                stats.resetAtWorker();
                stats.add(data);
                return stats;
            }
        });
        return future;
    }
}
