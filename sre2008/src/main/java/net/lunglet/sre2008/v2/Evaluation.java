package net.lunglet.sre2008.v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.Gender;
import net.lunglet.sre2008.Model;
import net.lunglet.sre2008.Segment;
import net.lunglet.sre2008.Trial;
import net.lunglet.util.MainTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

public final class Evaluation {
    @CommandLineInterface(application = "Evaluation")
    private static interface Arguments {
        @Option(shortName = "d", description = "evaluation supervectors")
        File getData();

        @Option(shortName = "m", description = "evaluation models")
        File getModels();

        @Option(shortName = "n", description = "evaluation trials index")
        File getNdx();

        @Option(shortName = "o", description = "output")
        File getOutput();

        @Option(shortName = "z", description = "tnorm models")
        File getTnorm();

        @Option(shortName = "t", description = "evaluation trn index")
        File getTrn();
    }

    private static class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File dataFile = args.getData();
            checkFileExists("SVM data", dataFile);
            File modelsFile = args.getModels();
            checkFileExists("GMM data", modelsFile);
            File tnormFile = args.getTnorm();
            checkFileExists("tnorm data", tnormFile);
            File trnFile = args.getTrn();
            checkFileExists("evaluation trn index", trnFile);
            File ndxFile = args.getNdx();
            checkFileExists("evaluation trials index", ndxFile);
            File outputFile = args.getOutput();
            if (false) {
                checkFileNotExists("output", outputFile);
            }
            LOGGER.info("Reading models from {}", trnFile);
            Map<String, Model> modelsMap = readModels(trnFile);
            LOGGER.info("Reading trials from {}", ndxFile);
            readTrials(ndxFile, modelsMap);
            LOGGER.info("Checking model file {}", modelsFile);
            LOGGER.info("Checking data file {}", dataFile);
            checkData(modelsMap, modelsFile, dataFile);

            LOGGER.info("Reading tnorm models from {}", tnormFile);
            FloatDenseMatrix tnormModels = readTNormModels(tnormFile);
            Map<String, double[]> tnormCache = new HashMap<String, double[]>();

            List<String> output = new ArrayList<String>();
            HDFReader modelReader = new HDFReader(modelsFile, 0);
            int modelDim = tnormModels.columns();
            for (Model model : modelsMap.values()) {
                LOGGER.info("Evaluating {} trials for model {}", model.getTest().size(), model.getId());
                FloatDenseVector speakerModel = DenseFactory.floatRowDirect(modelDim);
                modelReader.read(model.getId(), speakerModel);
                output.addAll(score(model, speakerModel, dataFile, tnormModels, tnormCache));
            }
            modelReader.close();

            Collections.sort(output);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
            for (String line : output) {
                outputWriter.write(line);
                outputWriter.write("\n");
            }
            outputWriter.close();

            return 0;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    private static void checkData(final Map<String, Model> modelsMap, final File modelsFile, final File dataFile) {
        H5File modelsh5 = new H5File(modelsFile);
        H5File datah5 = new H5File(dataFile);
        for (Model model : modelsMap.values()) {
            if (!modelsh5.getRootGroup().existsDataSet(model.getId())) {
                throw new RuntimeException(model.getId() + " is missing from " + modelsFile);
            }
            for (Trial trial : model.getTest()) {
                if (!datah5.getRootGroup().existsDataSet(trial.getHDFName())) {
                    throw new RuntimeException(trial + " is missing from " + dataFile);
                }
            }
        }
        datah5.close();
        modelsh5.close();
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }

    public static Map<String, Model> readModels(final File trnFile) throws IOException {
        Map<String, Model> modelsMap = new HashMap<String, Model>();
        BufferedReader reader = new BufferedReader(new FileReader(trnFile));
        try {
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 6) {
                    throw new RuntimeException();
                }
                String id = parts[0].toLowerCase();
                if (modelsMap.containsKey(id)) {
                    throw new RuntimeException("Duplicate model ID: " + id);
                }
                Gender gender = Gender.valueOf2(parts[1]);

                List<Segment> train = new ArrayList<Segment>();
                for (String trainStr : parts[2].split(",")) {
                    String[] trainParts = trainStr.split(":");
                    train.add(new Segment(trainParts[0], trainParts[1]));
                }
                if (train.size() < 1) {
                    throw new RuntimeException();
                }
                Model model = new Model(id, gender, train);
                model.setProperty("language", parts[3]);
                model.setProperty("speechType", parts[4]);
                model.setProperty("channelType", parts[5]);
                modelsMap.put(id, model);
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return modelsMap;
    }

    public static FloatDenseMatrix readTNormModels(final File tnormFile) {
        H5File tnormh5 = new H5File(tnormFile);
        Set<String> tnormNames = tnormh5.getRootGroup().getDataSetNames();
        DataSet tnormds = tnormh5.getRootGroup().openDataSet(tnormNames.iterator().next());
        int[] modelDims = tnormds.getIntDims();
        tnormds.close();
        // TODO make this a utility method somewhere
        int vecDim = modelDims.length == 1 ? modelDims[0] : Math.max(modelDims[0], modelDims[1]);
        int[] dims = {tnormNames.size(), vecDim};
        FloatDenseMatrix tnormModels = DenseFactory.floatRowDirect(dims);
        HDFReader reader = new HDFReader(tnormh5);
        int i = 0;
        for (String name : tnormNames) {
            reader.read(name, tnormModels.row(i++));
        }
        reader.close();
        return tnormModels;
    }

    private static void readTrials(final File ndxFile, final Map<String, Model> modelsMap) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(ndxFile));
        try {
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 6) {
                    throw new RuntimeException();
                }
                String id = parts[0];
                if (!modelsMap.containsKey(id)) {
                    throw new RuntimeException("Unknown model ID in trial: " + id);
                }
                Model model = modelsMap.get(id);
                Gender gender = Gender.valueOf2(parts[1]);
                if (!model.getGender().equals(gender)) {
                    throw new RuntimeException("Gender mismatch in trial for model " + id);
                }
                String[] testParts = parts[2].split(":");
                String name = testParts[0];
                String channel = testParts[1];
                final String answer;
                if (parts.length >= 7) {
                    answer = parts[6].toLowerCase();
                } else {
                    answer = "unknown";
                }
                Trial trial = new Trial(name, channel, answer);
                trial.setProperty("language", parts[3]);
                trial.setProperty("speechType", parts[4]);
                trial.setProperty("channelType", parts[5]);
                model.addTrial(trial);
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
    }

    public static List<String> score(final Model model, final FloatVector speakerModel, final File dataFile,
            final FloatDenseMatrix tnormModels, final Map<String, double[]> tnormCache) {
        HDFReader reader = new HDFReader(dataFile, 0);
        FloatDenseVector x = DenseFactory.floatColumnDirect(speakerModel.length());
        FloatDenseVector buf = DenseFactory.floatColumnDirect(speakerModel.length() - 1);
        List<String> output = new ArrayList<String>();
        for (Trial trial : model.getTest()) {
            final double score;
            if (!Answer.BAD.equals(trial.getAnswer())) {
                // read trial data into buffer and prepare vector for scoring
                // using a single dot product or gemv
                reader.read(trial.getHDFName(), buf);
                FloatBuffer xdata = x.data();
                xdata.put(buf.data());
                xdata.put(-1.0f);

                // score the model against the trial
                float modelScore = FloatMatrixMath.dot(speakerModel, x);

                // normalize the score using tnorm
                if (tnormModels != null) {
                    final double[] tnormParams;
                    if (tnormCache.containsKey(trial.getHDFName())) {
                        LOGGER.debug("Found tnorm parameters for {} in cache", trial.getHDFName());
                        tnormParams = tnormCache.get(trial.getHDFName());
                    } else {
                        float[] tnormScores = FloatMatrixMath.times(tnormModels, x).toArray();
                        tnormParams = ScoreUtils.getParams(tnormScores);
                        tnormCache.put(trial.getHDFName(), tnormParams);
                    }
                    double mean = tnormParams[0];
                    double stddev = tnormParams[1];
                    score = (modelScore - mean) / stddev;
                } else {
                    score = modelScore;
                }
            } else {
                LOGGER.warn("Trial {} for model {} is bad", trial, model.getId());
                score = Double.NaN;
            }

            List<String> parts = new ArrayList<String>();
            parts.add(model.getId().toLowerCase() + "_" + trial.getName() + ":" + trial.getChannel());
            parts.add(model.getGender().name().substring(0, 1).toLowerCase());
            // append sideinfo
            for (String key : new String[]{"language", "channelType"}) {
                String modelValue = model.getProperty(key);
                String trialValue = trial.getProperty(key);
                parts.add(modelValue + ":" + trialValue);
            }
            parts.add(trial.getAnswerString());
            parts.add(String.format("%.15E", score));

            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                strBuilder.append(parts.get(i));
                if (i < parts.size() - 1) {
                    strBuilder.append(" ");
                }
            }
            String str = strBuilder.toString();
            output.add(str);
        }
        reader.close();
        return output;
    }
}
