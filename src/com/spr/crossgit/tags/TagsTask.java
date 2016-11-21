package com.spr.crossgit.tags;

import com.spr.crossgit.api.IGitRepository;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.eclipse.jgit.lib.Ref;

public class TagsTask extends Task<ObservableList<Ref>> {

    private final IGitRepository repo;

    TagsTask(IGitRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ObservableList<Ref> call() throws Exception {
        return repo.getTags();
    }

}
