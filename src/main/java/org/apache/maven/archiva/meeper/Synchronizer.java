package org.apache.maven.archiva.meeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * exclusions=$HOME/bin/synchronize/syncopate/exclusions.txt
 * BASEDIR=$HOME/repository-staging/to-ibiblio/maven2
 * CHANGED_LOG=/tmp/sync-changed.log
 */
public class Synchronizer {

    private static final String RSYNC = "rsync";

    private static final String DRY_RUN = "-n";

    /** timeout in seconds for each repo */
    private static final int TIMEOUT = 5 * 60;

    private SynchronizerOptions options;

    private List failedRepositories = new ArrayList();

    public Synchronizer(SynchronizerOptions options) {
        this.options = options;
    }

    public void sync(List repositories) {
        Iterator it = repositories.iterator();
        while (it.hasNext()) {
            SyncedRepository repo = (SyncedRepository) it.next();
            try {
                sync(repo);
            } catch (RuntimeException e) {
                System.out.println("Error synchronizing repository " + repo.getGroupId() + ". "
                        + e.getMessage());
                failedRepositories.add(repo);
            } catch (InterruptedException e) {
                System.out.println("Repository sync for " + repo.getGroupId() + " was interrupted");
            }
        }
    }

    public void sync(SyncedRepository repo) throws InterruptedException {
        int exitCode = syncMetadata(repo);
        if (exitCode != 0) {
            throw new RuntimeException("Error synchronizing metadata. Exit code: " + exitCode);
        }
        exitCode = syncArtifacts(repo);
        if (exitCode != 0) {
            throw new RuntimeException("Error synchronizing artifacts. Exit code: " + exitCode);
        }
    }

    private int syncMetadata(SyncedRepository repo) throws InterruptedException {
        Commandline cl = new Commandline();
        cl.setExecutable(RSYNC);

        cl.createArg().setValue("--include=*/");
        cl.createArg().setValue("--include=**/maven-metadata.xml*");
        cl.createArg().setValue("--exclude=*");
        cl.createArg().setValue("--exclude-from=" + options.getExclusionsFile());
        addCommonArguments(cl, repo);

        System.out.println("=== Synchronizing metadata " + repo.getGroupId() + " "
                + repo.getLocation());
        return executeCommandLine(cl, repo);
    }

    private int syncArtifacts(SyncedRepository repo) {
        Commandline cl = new Commandline();
        cl.setExecutable(RSYNC);

        cl.createArg().setValue("--exclude-from=" + options.getExclusionsFile());
        cl.createArg().setValue("--ignore-existing");
        addCommonArguments(cl, repo);

        System.out.println("=== Synchronizing metadata " + repo.getGroupId() + " "
                + repo.getLocation());
        return executeCommandLine(cl, repo);
    }

    private void addCommonArguments(Commandline cl, SyncedRepository repo) {
        if (options.isDryRun()) {
            cl.createArg().setValue(DRY_RUN);
        }
        // cl.createArg().setValue("$RSYNC_OPTS");
        cl.createArg().setValue("-Lrtivz");
        if (SyncedRepository.PROTOCOL_SSH.equals(repo.getProtocol())) {
            String s = repo.getSshOptions() == null ? "" : repo.getSshOptions();
            cl.createArg().setValue("--rsh=ssh " + s);
        }

        String groupDir = repo.getGroupId().replaceAll("\\.", "\\/");
        cl.createArg().setValue(repo.getLocation() + "/" + groupDir + "/");
        cl.createArg().setValue(options.getBasedir() + "/" + groupDir + "/");
    }

    private int executeCommandLine(Commandline cl, SyncedRepository repo) {
        CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();
        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();

        // System.out.println("About to execute " + cl);

        int exitCode;
        try {
            exitCode = CommandLineUtils.executeCommandLine(cl, out, err);
        } catch (CommandLineException e) {
            throw new RuntimeException(e);
        }

        repo.setOut(out.getOutput());

        String serr = err.getOutput();
        if ((serr != null) && serr.length() > 0) {
            repo.setErr(serr);
        }

        return exitCode;
    }

    public static void main(String[] args) {
        if ((args.length != 2) && (args.length != 3)) {
            System.out.println("Arguments required: CONFIG_PROPERTIES_FILE REPOSITORIES_FILE [go]");
            return;
        }

        int i = 0;
        SynchronizerOptions options = SynchronizerOptions.parse(new File(args[i++]));
        Synchronizer synchronizer = new Synchronizer(options);

        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(args[i++]));
        } catch (FileNotFoundException e) {
            System.err.println("Repositories file " + args[i - 1] + " is not present");
        }

        List repositories;
        try {
            repositories = new CsvReader().parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        if (args.length == 3) {
            String go = args[i++];
            if ((go != null) && ("go".equals(go))) {
                options.setDryRun(false);
            }
        }

        synchronizer.sync(repositories);

        if (synchronizer.failedRepositories.isEmpty()) {
            System.out.println("--- All repositories synchronized successfully ---");
        } else {
            System.out.println("--- Some repositories were not synchronized ---");
            Iterator it = synchronizer.failedRepositories.iterator();
            while (it.hasNext()) {
                SyncedRepository repo = (SyncedRepository) it.next();
                System.out.println(" * " + repo.getGroupId());
                System.out.println(repo.getErr());
            }
        }
    }
}
