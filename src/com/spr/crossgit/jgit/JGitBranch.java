package com.spr.crossgit.jgit;

import com.spr.crossgit.api.IGitBranch;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class JGitBranch implements IGitBranch {

    private final Ref ref;
    private final RevCommit commit;
    private final boolean isCurrent;

    JGitBranch(Ref ref, Repository repo) {
        this.ref = ref;
        commit = getCommit(repo, ref);
        isCurrent = isCurrentBranch(repo);
    }

    private RevCommit getCommit(Repository repo, Ref ref) {
        try (RevWalk walk = new RevWalk(repo)) {
            return walk.parseCommit(ref.getObjectId());
        } catch (IncorrectObjectTypeException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getName() {
        return ref.getName().replaceAll("refs/heads/", "");
    }

    @Override
    public AnyObjectId getId() {
        return ref.getObjectId();
    }

    @Override
    public int getCommitTime() {
        return commit.getCommitTime();
    }

    private boolean isCurrentBranch(Repository repo) {
        try {
            Ref currentRef = repo.exactRef(repo.getFullBranch());
            return ref.getObjectId().equals(currentRef.getObjectId());
        } catch (IOException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean isCurrent() {
        return isCurrent;
    }

}
