package net.lunglet.sre2008.v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.lunglet.sre2008.Gender;
import net.lunglet.sre2008.Model;
import net.lunglet.util.MainTemplate;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

public final class Evaluation {
    @CommandLineInterface(application = "Evaluation")
    private static interface Arguments {
        @Option(shortName = "g", description = "GMM data")
        File getGmmData();

        @Option(description = "trials index")
        File getNdx();

        @Option(shortName = "o", description = "output")
        File getOutput();

        @Option(shortName = "s", description = "SVM data")
        File getSvmData();

        @Option(shortName = "t", description = "tnorm data")
        File getTnormData();

        @Option(description = "models index")
        File getTrn();
    }

    private static class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File svmFile = args.getGmmData();
            checkFileExists("SVM data", svmFile);
            File gmmFile = args.getGmmData();
            checkFileExists("GMM data", gmmFile);
            File tnormFile = args.getTnormData();
            checkFileExists("tnorm data", tnormFile);
            File trnFile = args.getTrn();
            checkFileExists("models", trnFile);
            File ndxFile = args.getNdx();
            checkFileExists("trials", ndxFile);
            File outputFile = args.getOutput();
            checkFileNotExists("output", outputFile);

            Map<String, Model> modelsMap = readModels(trnFile);
            readTrials(ndxFile, modelsMap);

            // TODO check that all svms for models are available
            // TODO check that all gmms for trials are available

            return 0;
        }
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }

    private static Map<String, Model> readModels(final File trnFile) throws IOException {
        Map<String, Model> modelsMap = new HashMap<String, Model>();
        BufferedReader reader = new BufferedReader(new FileReader(trnFile));
        try {
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 6) {
                    throw new RuntimeException();
                }
                String id = parts[0];
                Gender gender = Gender.valueOf2(parts[1]);
                String[] train = parts[2].split(",");
                if (train.length < 1) {
                    throw new RuntimeException();
                }
                String language = parts[3];
                String speechType = parts[4];
                String channelType = parts[5];

                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return modelsMap;
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
                    throw new RuntimeException("Unknown model id in trial: " + id);
                }
                Gender gender = Gender.valueOf2(parts[1]);
                String test = parts[2];
                String language = parts[3];
                String speechType = parts[4];
                String channelType = parts[5];
                final String answer;
                if (parts.length >= 7) {
                    answer = parts[6].toLowerCase();
                    if (!answer.equals("target") && !answer.equals("nontarget")) {
                        throw new RuntimeException("Invalid answer in trial: " + answer);
                    }
                } else {
                    answer = "unknown";
                }
                // TODO associate trial with model
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
    }
}
