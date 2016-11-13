package com.spr.crossgit.repo.local;

import com.spr.crossgit.Prefs;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

public class StatusTask extends Task<String> {

    private final Repository repo;

    StatusTask(Repository repo) {
        this.repo = repo;
    }

    @Override
    protected String call() throws Exception {

        // Allows time to show visual indication when branch name
        // is clicked to refresh status.
        Thread.sleep(100);

        try (Git git = new Git(repo)) {
            // potentially, this can take a while.
            Status status = git.status().call();
//            debugStatus(status);
            return status.hasUncommittedChanges()
                    ? "[ uncommitted changes ]" : "";
        } catch (GitAPIException | NoWorkTreeException ex) {
            Logger.getLogger(LocalRepoPane.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }

    private void debugStatus(Status status) {
        if (Prefs.isDevMode) {
            System.out.println("Added: " + status.getAdded());
            System.out.println("Changed: " + status.getChanged());
            System.out.println("Conflicting: " + status.getConflicting());
            System.out.println("ConflictingStageState: " + status.getConflictingStageState());
            System.out.println("IgnoredNotInIndex: " + status.getIgnoredNotInIndex());
            System.out.println("Missing: " + status.getMissing());
            System.out.println("Modified: " + status.getModified());
            System.out.println("Removed: " + status.getRemoved());
            System.out.println("Untracked: " + status.getUntracked());
            System.out.println("UntrackedFolders: " + status.getUntrackedFolders());
            System.out.println("hasUncommittedChanges: " + status.hasUncommittedChanges());
        }
    }

}
