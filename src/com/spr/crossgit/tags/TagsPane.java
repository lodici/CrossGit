package com.spr.crossgit.tags;

import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.api.IGitTag;
import com.spr.crossgit.screen.MainScreen;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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

    public void setRepo(IGitRepository repo) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        tagsList.setItems(FXCollections.emptyObservableList());
        task = new TagsTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            final ObservableList<IGitTag> tags = task.getValue();
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
