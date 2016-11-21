package com.spr.crossgit.screen.commit;

import com.spr.crossgit.api.IGitRepository;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

class ChangeSetTask extends Task<ObservableList<String>> {

    private final IGitRepository repo;

    ChangeSetTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ObservableList<String> call() throws Exception {
        return repo.getChangesetFiles();
    }

}
