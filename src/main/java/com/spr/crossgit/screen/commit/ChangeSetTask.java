package com.spr.crossgit.screen.commit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Repository;

class ChangeSetTask extends Task<ObservableList<String>> {

    private final Repository repo;

    ChangeSetTask(Repository repo) {
        this.repo = repo;
    }    

    @Override
    protected ObservableList<String> call() throws Exception {
        final List<String> files = new ArrayList<>();
        try (Git git = new Git(repo)) {
            Status status = git.status().call();
//            status.getAdded().forEach(f -> files.add(f + " [A]"));
//            status.getChanged().forEach(f -> files.add(f + " [C]"));
//            status.getMissing().forEach(f -> files.add(f + " [X ]"));
//            status.getModified().forEach(f -> files.add(f + " [M]"));
//            status.getRemoved().forEach(f -> files.add(f + " [R]"));
            status.getUncommittedChanges().forEach(f -> files.add(f));
            status.getUntracked().forEach(f -> files.add(f));
        }
        Collections.sort(files);
        return FXCollections.observableList(files);
    }

}
