package com.spr.crossgit.changeset;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.api.IGitRepository;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import static javafx.scene.layout.VBox.setVgrow;

public class ChangeSetPane extends VBox {

    private final ChangeSetTable filesTable;
    private final Label headerLabel;
    private ExecutorService executor;
    private ChangeSetTask task;

    public ChangeSetPane() {
        this.filesTable = new ChangeSetTable();
        this.headerLabel = new Label();
        headerLabel.setPrefHeight(20.0);
        headerLabel.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font: 14px sans-serif;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 6px;"
        );
        setVgrow(filesTable, Priority.ALWAYS);
        getChildren().addAll(headerLabel, filesTable);
    }

    // used to remove the default "No content in table" placeholder text.
    private static final Label NO_PLACEHOLDER = new Label();

    public void setCommit(IGitRepository repo, GitCommit commit) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        filesTable.setItems(FXCollections.emptyObservableList());
        filesTable.hideHeader();
        filesTable.setPlaceholder(NO_PLACEHOLDER);
        task = new ChangeSetTask(repo, commit);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            assert Platform.isFxApplicationThread();
            final ObservableList<ChangeSetFile> files = task.getValue();
            filesTable.setItems(files);
            headerLabel.setText(String.format("Changes: %s",
                    files != null
                            ? NumberFormat.getInstance().format(files.size())
                            : 0
            ));
        });
        task.setOnCancelled((WorkerStateEvent event) -> {
            Logger.getLogger(ChangeSetPane.class.getName()).log(Level.INFO, "Task cancelled");
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            Logger.getLogger(ChangeSetPane.class.getName())
                    .log(Level.WARNING, "Task failed", event.getSource().getException());
        });

        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }

}
