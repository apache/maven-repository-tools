package org.apache.maven.archiva.meeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SynchronizerOptions {
    private static final String BASEDIR = "basedir";
    private static final String LOG_FILE = "log";
    private static final String EXCLUSIONS_FILE = "exclusionsFile";
    private String exclusionsFile;
    private String basedir;
    private String logFile;
    private boolean dryRun = true;

    public String getExclusionsFile() {
        return exclusionsFile;
    }

    public void setExclusionsFile(String exclusionsFile) {
        this.exclusionsFile = exclusionsFile;
    }

    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public static SynchronizerOptions parse(File f) {

        FileReader reader;
        try {
            reader = new FileReader(f);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File does not exist: " + f, e);
        }

        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }

        SynchronizerOptions options = new SynchronizerOptions();
        options.setExclusionsFile(properties.getProperty(EXCLUSIONS_FILE));
        options.setLogFile(properties.getProperty(LOG_FILE));
        options.setBasedir(properties.getProperty(BASEDIR));
        return options;
    }
}
