package net.lunglet.util;

import java.io.File;
import java.util.List;
import java.util.Random;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

public final class MainTemplateTest {
    @CommandLineInterface(application="MainTemplateTest")
    private static interface Arguments {
        @Option(shortName="f", description="filelist")
        File getFilelist();

        @Unparsed(name="FILE")
        List<File> getFiles();

        @Option(shortName="n", description="trials")
        File getNdx();

        @Option(shortName="t", description="models")
        File getTrn();
    }

    public static void main(final String[] args) throws Throwable {
        new MainTemplate<Arguments>(Arguments.class) {
            @Override
            protected int mainImpl(final Arguments args) throws Throwable {
                checkFileExists("filelist", args.getFilelist());
                checkFileExists("trials", args.getNdx());
                checkFileExists("models", args.getTrn());
                if (new Random().nextDouble() > 0.5) {
                    throw new Throwable();
                }
                return 0;
            }
        }.main(args);
    }
}
