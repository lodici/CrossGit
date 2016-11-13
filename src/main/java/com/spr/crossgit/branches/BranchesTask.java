package com.spr.crossgit.branches;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;

class BranchesTask extends Task<BranchesInfo> {

    private final Repository repo;

    BranchesTask(Repository repo) {
        this.repo = repo;
    }

    private String containsCommitish;

    private Collection<Ref> filterRefs(Collection<Ref> refs)
            throws RefNotFoundException, IOException {
        if (containsCommitish == null) {
            return refs;
        }

        try (RevWalk walk = new RevWalk(repo)) {
            ObjectId resolved = repo.resolve(containsCommitish);
            if (resolved == null) {
                throw new RefNotFoundException(MessageFormat.format(
                        JGitText.get().refNotResolved, containsCommitish));
            }

            RevCommit containsCommit = walk.parseCommit(resolved);
            return RevWalkUtils.findBranchesReachableFrom(containsCommit, walk,
                    refs);
        }
    }

    @Override
    protected BranchesInfo call() throws Exception {

//        Collection<Ref> refs = filterRefs(
//                repo.getRefDatabase().getRefs(Constants.R_HEADS).values()
//        );
//        refs.forEach(r -> System.out.println(r));

        try (Git git = new Git(repo)) {
            ListBranchCommand cmd = git.branchList();
            List<Ref> branches = cmd.call();
            return new BranchesInfo(branches, repo, git);
        }
    }

}
