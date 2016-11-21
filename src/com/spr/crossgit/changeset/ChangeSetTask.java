package com.spr.crossgit.changeset;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.api.IGitRepository;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

class ChangeSetTask extends Task<ObservableList<ChangeSetFile>> {

    private final IGitRepository repo;
    private final GitCommit gitCommit;

    ChangeSetTask(IGitRepository repo, GitCommit commit) {
        this.repo = repo;
        this.gitCommit = commit;
    }

    @Override
    protected ObservableList<ChangeSetFile> call() throws Exception {
        return repo.getChangesetFiles(gitCommit);
    }

}
