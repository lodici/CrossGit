package com.spr.crossgit.api;

import org.eclipse.jgit.lib.AnyObjectId;

public interface IGitBranch {

    /**
     * Branch name without the 'refs/heads/' prefix.
     */
    public String getName();

    public AnyObjectId getId();

    /**
     * time, expressed as seconds since the epoch.
     */
    public int getCommitTime();

    /**
     * Returns true if branch is active.
     */
    public boolean isCurrent();
}
