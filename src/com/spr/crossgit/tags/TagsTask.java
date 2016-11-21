package com.spr.crossgit.tags;

import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.api.IGitTag;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class TagsTask extends Task<ObservableList<IGitTag>> {

    private final IGitRepository repo;

    TagsTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ObservableList<IGitTag> call() throws Exception {
        return repo.getTags();
    }

}
