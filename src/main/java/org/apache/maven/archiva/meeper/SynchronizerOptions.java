package org.apache.maven.archiva.meeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SynchronizerOptions {
    private static final String BASEDIR = "basedir";
    private static final String LOG_FILE = "log";
    private static final String EXCLUSIONS_FILE = "exclusionsFile";
    private static final String MAIL_HOST_NAME = "mailHostName";
    private static final String MAIL_TO = "mailTo";
    private static final String MAIL_FROM = "mailFrom";
    private static final String MAIL_SUBJECT = "mailSubject";
    private String exclusionsFile;
    private String basedir;
    private String logFile;
    private boolean dryRun = true;
    private String mailHostname, mailTo, mailFrom, mailSubject;

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

        FileInputStream is;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File does not exist: " + f, e);
        }

        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        SynchronizerOptions options = new SynchronizerOptions();
        options.setExclusionsFile(properties.getProperty(EXCLUSIONS_FILE));
        options.setLogFile(properties.getProperty(LOG_FILE));
        options.setBasedir(properties.getProperty(BASEDIR));
        options.setMailHostname(properties.getProperty(MAIL_HOST_NAME));
        options.setMailTo(properties.getProperty(MAIL_TO));
        options.setMailFrom(properties.getProperty(MAIL_FROM));
        options.setMailSubject(properties.getProperty(MAIL_SUBJECT));
        return options;
    }

    public void setMailHostname(String mailHostname) {
        this.mailHostname = mailHostname;
    }

    public String getMailHostname() {
        return mailHostname;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }
}
