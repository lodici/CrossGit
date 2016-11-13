package com.spr.crossgit.commits;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.screen.MainScreen;
import com.spr.crossgit.branches.BranchesInfo;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Repository;

public class CommitsPane extends VBox {

    private ExecutorService executor;
    private final CommitsTable commitsTable;
    private CommitsTask task;
    private final Label headerLabel;

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

    public void setRepo(Repository repo, BranchesInfo info) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        headerLabel.setText("Loading commits...");
        commitsTable.setItems(FXCollections.emptyObservableList());
        commitsTable.hideHeader();
        task = new CommitsTask(repo, info);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            final ObservableList<GitCommit> commits = task.getValue();
            commitsTable.setItems(commits);
            commitsTable.selectFirstRow();
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
}
