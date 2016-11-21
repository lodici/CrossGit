package com.spr.crossgit.api;

import org.eclipse.jgit.lib.AnyObjectId;

public interface IGitBranch {
    public String getName();
    public AnyObjectId getId();
}
