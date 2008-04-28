package net.lunglet.sre2008.v2;

import net.lunglet.util.MainTemplate;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;

public final class TrainSVM {
    @CommandLineInterface(application = "TrainSVM")
    private static interface Arguments {
    }

    public static final class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            return 0;
        }
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }
}
