package com.spr.crossgit;

import com.spr.crossgit.branches.BranchesInfo;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitCommit {

    //
    // see http://blog.netopyr.com/2012/02/02/creating-read-only-properties-in-javafx/
    //     http://stackoverflow.com/questions/31266498/when-to-use-stringproperty-over-string
    //
    private ReadOnlyStringWrapper message;
    private ReadOnlyStringWrapper committedOn;
    private final RevCommit revCommit;
    private final Git git;
    private final BranchesInfo branchInfo;

    public GitCommit(RevCommit revCommit, Git git, BranchesInfo info) {
        this.revCommit = revCommit;
        this.git = git;
        this.branchInfo = info;
    }

    private ReadOnlyStringWrapper getCommitTimestamp() {
        Date commitDate = new Date(revCommit.getCommitTime() * 1000L);
        LocalDateTime start = commitDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        boolean isRecent = Duration.between(start, LocalDateTime.now()).toHours() <= 24L;
        return new ReadOnlyStringWrapper(
                new SimpleDateFormat(isRecent ? "HH:mm" : "yyyy-MM-dd")
                        .format(commitDate));
    }

    public final String getAuthor() {
        return revCommit.getAuthorIdent().getName();
    }
   
    public final String getMessage() {
        return messageProperty().get();
    }

    public final ReadOnlyStringProperty messageProperty() {
        if (message == null) {
            message = new ReadOnlyStringWrapper(revCommit.getShortMessage());
        }
        return message.getReadOnlyProperty();
    }

    public final String getCommittedOn() {
        if (committedOn == null) {
            committedOn = getCommitTimestamp();
        }
        return committedOn.get();
    }

    public ObjectId getId() {
        return revCommit.getId();
    }

    public String getCommitHash() {
        return revCommit.getId().getName();
    }

    public List<String> getBranchList() {
        ListBranchCommand cmd = git.branchList();
        cmd.setContains(getCommitHash());
        try {
            return cmd.call().stream()
                    .map(r -> r.getName())
                    .collect(Collectors.toList());
        } catch (GitAPIException ex) {
            Logger.getLogger(GitCommit.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<Ref> getHeadRefs() {
        return branchInfo.getHeadRefs(getCommitHash());
    }

    public List<String> getTagsList() {
        ListTagCommand cmd = git.tagList();
        return new ArrayList<>();
    }

    public boolean isOnActiveBranch() {
        return branchInfo.isOnActiveBranch(revCommit);
    }

}
