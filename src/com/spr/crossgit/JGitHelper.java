package com.spr.crossgit;

import org.eclipse.jgit.lib.Ref;

public final class JGitHelper {

    public static String getHash(Ref ref) {
        try {
            return ref.getObjectId().getName();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    private JGitHelper() {}
}
