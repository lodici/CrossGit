package com.spr.crossgit.branches;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class BranchesInfo {

    private class BranchCommit {
        Ref branchRef;
        RevCommit commitRef;
        private BranchCommit(RevCommit revCommit, Ref ref) {
            this.commitRef = revCommit;
            this.branchRef = ref;
        }
    }

    // list of branch refs from branchList().
    private final List<Ref> refs;

    // list of branch refs by commit hash.
    // can have more than one branch associated with a commit.
    private Map<String, List<Ref>> refMap;

    // for each branch store its associated RecCommit.
    private List<BranchCommit> commits;

    private Ref currentBranch;

    BranchesInfo(List<Ref> branches, Repository repo, Git git) {
        assert !Platform.isFxApplicationThread();
        this.refs = new ArrayList(branches);
        setRefsMap();
        try {
            this.currentBranch = repo.exactRef(repo.getFullBranch());
            setCommitsMap(repo);
        } catch (IOException ex) {
            Logger.getLogger(BranchesInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setCommitsMap(Repository repo) throws IOException {
        this.commits = new ArrayList<>();
        try (RevWalk walk = new RevWalk(repo)) {
            for (Ref ref : refs) {
                RevCommit revCommit = walk.parseCommit(ref.getObjectId());
                commits.add(new BranchCommit(revCommit, ref));
            }
            walk.dispose();
        }
    }

    private void setRefsMap() {
        this.refMap = new HashMap<>(refs.size());
        for (Ref ref : refs) {
            final String key = ObjectId.toString(ref.getObjectId());
            if (refMap.containsKey(key)) {
                final List<Ref> refList = refMap.get(key);
                refList.add(ref);
            } else {
                final List<Ref> refList = new ArrayList<>();
                refList.add(ref);
                refMap.put(key, refList);
            }
        }
    }

    public ObservableList<Ref> getRefsList() {
        return FXCollections.observableArrayList(commits.stream()
                .sorted((c1, c2) -> c2.commitRef.getCommitTime() - c1.commitRef.getCommitTime())
                .map(c -> c.branchRef)
                .collect(Collectors.toList())
        );
//        return FXCollections.observableArrayList(refs);
    }

    public List<Ref> getHeadRefs(String commitHash) {
        return refMap.containsKey(commitHash)
                ? refMap.get(commitHash)
                : new ArrayList<>();
    }

    public boolean isOnActiveBranch(RevCommit revCommit) {
        return revCommit.getId().equals(currentBranch.getObjectId());
    }
}
