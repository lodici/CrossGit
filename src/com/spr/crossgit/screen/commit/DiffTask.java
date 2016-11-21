package com.spr.crossgit.screen.commit;

import com.spr.crossgit.api.IGitRepository;
import javafx.concurrent.Task;

public class DiffTask extends Task<String> {

    private final IGitRepository repo;
    private final String filePath;

    public DiffTask(IGitRepository repo, String filePath) {
        this.repo = repo;
        this.filePath = filePath;
    }

    @Override
    protected String call() throws Exception {
        return repo.getUnifiedDiff(filePath);
    }

}
