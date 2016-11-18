package com.spr.crossgit.branches;

import java.util.List;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

class BranchesTask extends Task<BranchesInfo> {

    private final Repository repo;

    BranchesTask(Repository repo) {
        this.repo = repo;
    }

    @Override
    protected BranchesInfo call() throws Exception {
        try (Git git = new Git(repo)) {
            ListBranchCommand cmd = git.branchList();
            List<Ref> branches = cmd.call();
            return new BranchesInfo(branches, repo, git);
        }
    }

}
