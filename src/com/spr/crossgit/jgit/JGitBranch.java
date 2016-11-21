package com.spr.crossgit.jgit;

import com.spr.crossgit.api.IGitBranch;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Ref;

public class JGitBranch implements IGitBranch {

    private final Ref ref;

    JGitBranch(Ref ref) {
        this.ref = ref;
    }

    @Override
    public String getName() {
        return ref.getName().replaceAll("refs/heads/", "");
    }

    @Override
    public AnyObjectId getId() {
        return ref.getObjectId();
    }

}
