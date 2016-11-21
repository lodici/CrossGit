package com.spr.crossgit.branches;

import com.spr.crossgit.api.BranchSortOrder;
import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitRepository;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

class BranchesTask extends Task<ObservableList<IGitBranch>> {

    private final IGitRepository repo;

    BranchesTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ObservableList<IGitBranch> call() throws Exception {
        return repo.getBranches(BranchSortOrder.getValue());
    }

}
