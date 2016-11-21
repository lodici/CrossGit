package com.spr.crossgit.jgit;

import com.spr.crossgit.api.IGitTag;
import org.eclipse.jgit.lib.Ref;

public class JGitTag implements IGitTag {

    private final Ref ref;

    JGitTag(Ref ref) {
        this.ref = ref;
    }

    @Override
    public String getName() {
        return ref.getName().replaceAll("refs/tags/", "");
    }

}
