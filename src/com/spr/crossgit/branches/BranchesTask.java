package com.spr.crossgit.branches;

import com.spr.crossgit.api.IGitRepository;
import javafx.concurrent.Task;

class BranchesTask extends Task<BranchesInfo> {

    private final IGitRepository repo;

    BranchesTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected BranchesInfo call() throws Exception {
        return repo.getBranches();
    }

}
