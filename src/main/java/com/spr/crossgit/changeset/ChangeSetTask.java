package com.spr.crossgit.changeset;

import com.spr.crossgit.GitCommit;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

class ChangeSetTask extends Task<ObservableList<ChangeSetFile>> {

    private final Repository repo;
    private final GitCommit gitCommit;

    ChangeSetTask(Repository repo, GitCommit commit) {
        this.repo = repo;
        this.gitCommit = commit;
    }    

    @Override
    protected ObservableList<ChangeSetFile> call() throws Exception {
        try (RevWalk rw = new RevWalk(repo)) {
            RevCommit revCommit = rw.parseCommit(gitCommit.getId());
            if (revCommit.getParentCount() > 0) {
                RevCommit parent = rw.parseCommit(revCommit.getParent(0).getId());
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                df.setRepository(repo);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), revCommit.getTree());
                return FXCollections.observableArrayList(diffs.stream()
                        .map(d -> new ChangeSetFile(d))
                        .collect(Collectors.toList()));
            } else {
                return FXCollections.emptyObservableList();
            }
        }
    }

}
