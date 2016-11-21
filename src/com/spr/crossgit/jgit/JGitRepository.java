package com.spr.crossgit.jgit;

import com.spr.crossgit.api.IGitRepository;
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class JGitRepository implements IGitRepository<Repository> {

    private final Repository repo;

    public JGitRepository(File f) throws IOException {
        repo = new FileRepositoryBuilder()
            .setGitDir(f)
            .setMustExist(true)
            .build();
    }

    @Override
    public String getBranch() {
        try {
            return repo.getBranch();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasUnCommittedChanges() {
        try (Git git = new Git(repo)) {
            return git.status().call().hasUncommittedChanges();
        } catch (GitAPIException | NoWorkTreeException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Repository get() {
        return repo;
    }
}
