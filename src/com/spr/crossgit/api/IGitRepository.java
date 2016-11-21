package com.spr.crossgit.api;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.changeset.ChangeSetFile;
import java.util.List;
import javafx.collections.ObservableList;

public interface IGitRepository {
    public String getBranch();
    public boolean hasUnCommittedChanges();
    public String getRemoteUrl();
    public ObservableList<String> getChangesetFiles();
    public ObservableList<ChangeSetFile> getChangesetFiles(GitCommit gitCommit);
    public ObservableList<IGitBranch> getBranches();
    public ObservableList<IGitTag> getTags();
    public ObservableList<IGitCommit> getAllCommits();
    public String getUnifiedDiff(String filePath);
    public boolean isCurrentBranch(IGitBranch branch);
    public List<IGitBranch> getBranchHeadsAt(IGitCommit commit);
    public List<IGitTag> getTagsAt(IGitCommit commit);
    public boolean isCurrentBranchHead(IGitCommit commit);
}
