package com.spr.crossgit.tags;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class TagsTask extends Task<ObservableList<Ref>> {

    private final Repository repo;

    TagsTask(Repository repo) {
        this.repo = repo;
    }

    @Override
    protected ObservableList<Ref> call() throws Exception {
        try (Git git = new Git(repo)) {
            ListTagCommand cmd = git.tagList();
            List<Ref> tags = cmd.call();
            return FXCollections.observableArrayList(tags);
        }
    }

}
