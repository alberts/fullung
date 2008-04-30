package net.lunglet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

// TODO extend readFilelist to support a FileValidator

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

    public final List<File> readFilelist(final File filelist) throws IOException {
        logger.info("Reading file list from {}", filelist);
        List<File> files = new ArrayList<File>();
        BufferedReader lineReader = new BufferedReader(new FileReader(filelist));
        try {
            String line = lineReader.readLine();
            while (line != null) {
                line = line.trim();
                if (!new File(line).isFile()) {
                    throw new FileNotFoundException(line + " is not a file");
                }
                files.add(new File(line));
                line = lineReader.readLine();
            }
            Collections.sort(files);
            return files;
        } finally {
            lineReader.close();
        }
    }
}
