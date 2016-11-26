package com.spr.crossgit.jgit;

import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitCommit;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Should do the bare minimum initialization.
  */
public class JGitCommit implements IGitCommit {

    private final RevCommit revCommit;

    public JGitCommit(RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    private String getCommitTimestamp() {
        Date commitDate = new Date(revCommit.getCommitTime() * 1000L);
        LocalDateTime start = commitDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        boolean isRecent = Duration.between(start, LocalDateTime.now()).toHours() <= 24L;
        return new SimpleDateFormat(isRecent ? "HH:mm" : "yyyy-MM-dd").format(commitDate);
    }

    @Override
    public String getCommittedOn() {
        return getCommitTimestamp();
    }

    @Override
    public String getAuthor() {
        return revCommit.getAuthorIdent().getName();
    }

    @Override
    public String getMessage() {
        return revCommit.getShortMessage();
    }

    @Override
    public Object getHeadRefs() {
        return "Not supported yet.";
    }

    @Override
    public boolean isHeadOf(IGitBranch branch) {
        return revCommit.getId().equals(branch.getId());
    }

    @Override
    public String getHash() {
        return revCommit.getId().getName();
    }

    RevCommit getRevCommit() {
        return revCommit;
    }
}
