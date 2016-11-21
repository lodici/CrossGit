package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitRepository;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class CommitsTask extends Task<ObservableList<IGitCommit>> {

    private final IGitRepository repo;

    CommitsTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ObservableList<IGitCommit> call() throws Exception {
        return repo.getAllCommits();
    }

}
