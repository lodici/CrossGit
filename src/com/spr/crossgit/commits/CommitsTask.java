package com.spr.crossgit.commits;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.branches.BranchesInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class CommitsTask extends Task<ObservableList<GitCommit>> {

    private final IGitRepository repo;
    private final BranchesInfo branches;

    CommitsTask(IGitRepository repo, BranchesInfo info) {
        this.repo = repo;
        this.branches = info;
    }

    @Override
    protected ObservableList<GitCommit> call() throws Exception {
        return repo.getAllCommits(branches);
    }

}
