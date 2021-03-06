package com.spr.crossgit.tags;

import com.spr.crossgit.screen.MainScreen;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class TagsPane extends VBox {

    private final TagsList tagsList;
    private TagsTask task;
    private ExecutorService executor;
    private final MainScreen app;

    public TagsPane(MainScreen app) {
        this.app = app;
        this.tagsList = new TagsList();
        setVgrow(tagsList, Priority.ALWAYS);
        getChildren().addAll(tagsList);
    }

    public void setRepo(Repository repo) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        tagsList.setItems(FXCollections.emptyObservableList());
        task = new TagsTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            final ObservableList<Ref> tags = task.getValue();
            tagsList.setItems(repo, tags);
            app.setTagsTotal(tags.size());
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }
}
