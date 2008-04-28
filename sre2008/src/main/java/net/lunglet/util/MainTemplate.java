package net.lunglet.util;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public abstract class MainTemplate<E> {
    protected static void checkFileExists(final String description, final File file) {
        if (!file.isFile()) {
            throw new RuntimeException("File for " + description + " doesn't exist: " + file);
        }
    }

    protected static void checkFileNotExists(final String description, final File file) {
        if (file.exists()) {
            throw new RuntimeException("File for " + description + " already exists: " + file);
        }
    }

    private final Class<E> argumentsClass;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public MainTemplate(final Class<E> argumentsClass) {
        this.argumentsClass = argumentsClass;
    }

    public void main(final String[] args) throws Throwable {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        Cli<E> cli = CliFactory.createCli(argumentsClass);
        try {
            logger.debug("Parsing arguments");
            E arguments = cli.parseArguments(args);
            int status = mainImpl(arguments);
            logger.debug("Exiting with status {}", status);
            System.exit(status);
        } catch (ArgumentValidationException e) {
            logger.debug("Argument validation failed");
            System.out.println(cli.getHelpMessage());
            System.exit(1);
        }
    }

    protected abstract int mainImpl(E args) throws Throwable;
}
