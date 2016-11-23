package com.spr.crossgit.commits;

import com.spr.crossgit.IBranchListener;
import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.screen.MainScreen;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CommitsPane extends VBox
    implements IBranchListener {

    private ExecutorService executor;
    private final CommitsTable commitsTable;
    private CommitsTask task;
    private final Label headerLabel;
    private IGitRepository repo;

    public CommitsPane(MainScreen app) {
        this.headerLabel = new Label();
        headerLabel.setPrefHeight(20.0);
        headerLabel.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font: 14px sans-serif;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 6px;"
        );
        this.commitsTable = new CommitsTable(app);
        setVgrow(commitsTable, Priority.ALWAYS);
        getChildren().addAll(headerLabel, commitsTable);
    }

    public void setRepo(IGitRepository repo, IGitBranch branch) {
        if (this.repo != null && this.repo == repo) {
            setBranch(branch);
            return;
        }
        this.repo = repo;

        if (task != null && task.isRunning()) {
            task.cancel();
        }
        headerLabel.setText("Loading commits...");
        commitsTable.setItems(FXCollections.emptyObservableList());
        commitsTable.hideHeader();
        task = new CommitsTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            final ObservableList<IGitCommit> commits = task.getValue();
            commitsTable.setItems(commits, repo);
            setBranch(branch);
            headerLabel.setText(String.format("Commits: %s",
                    NumberFormat.getInstance().format(commits.size()))
            );
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }

    @Override
    public void setBranch(IGitBranch branch) {
        Platform.runLater(() -> {
            commitsTable.select(branch);
        });
    }
}
