package com.spr.crossgit.commits;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.branches.BranchesInfo;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitsTask extends Task<ObservableList<GitCommit>> {

    private final Repository repo;
    private final BranchesInfo branchInfo;

    CommitsTask(Repository repo, BranchesInfo info) {
        this.repo = repo;
        this.branchInfo = info;
    }

    @Override
    protected ObservableList<GitCommit> call() throws Exception {
        try (Git git = new Git(repo)) {
            LogCommand cmd = git.log().all(); // .add(branchInfo.getRefsList().get(7).getObjectId());
            List<GitCommit> gitCommits = new ArrayList<>();
            Iterable<RevCommit> revCommits = cmd.call();
            for (RevCommit revCommit : revCommits) {
                final GitCommit gitCommit = new GitCommit(
                        revCommit, git, branchInfo
                );
                gitCommits.add(gitCommit);
            }
            return FXCollections.observableArrayList(gitCommits);
        }
    }

}
