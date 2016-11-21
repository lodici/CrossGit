package com.spr.crossgit.repo.local;

import com.spr.crossgit.Prefs;
import com.spr.crossgit.api.IGitRepository;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Status;

public class StatusTask extends Task<String> {

    private final IGitRepository repo;

    StatusTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected String call() throws Exception {

        // Allows time to show visual indication when branch name
        // is clicked to refresh status.
        Thread.sleep(100);

        return repo.hasUnCommittedChanges() ? "[ uncommitted changes ]" : "";
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
