package com.spr.crossgit.branches;

import com.spr.crossgit.IBranchListener;
import com.spr.crossgit.api.BranchSortOrder;
import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BranchesPane {

    private final VBox pane = new VBox();
    private final SortButtonBar sortBar;
    private final BranchesList branchesList = new BranchesList();
    private BranchesTask task;
    private ExecutorService executor;
    private IGitRepository repo;

    public BranchesPane() {
        sortBar = new SortButtonBar(this);
        sortBar.setPrefHeight(28.0);
        VBox.setVgrow(branchesList, Priority.ALWAYS);
        pane.getChildren().addAll(branchesList, sortBar.node());
    }

    public Node node() {
        return pane;
    }

    private void setRepo(IGitRepository repo, Runnable r) {
        this.repo = repo;
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        final IGitBranch selected = !branchesList.getItems().isEmpty()
            ? branchesList.getSelectionModel().getSelectedItem()
            : null;
        branchesList.setItems(FXCollections.emptyObservableList());
        task = new BranchesTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            branchesList.setItems(task.getValue());
            if (selected == null) {
                branchesList.getSelectionModel().select(0);
                branchesList.scrollTo(0);
            } else {
                branchesList.getSelectionModel().select(selected);
                branchesList.scrollTo(selected);
            }
            branchesList.requestFocus();
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }

    public void setRepo(IGitRepository repo) {
        setRepo(repo, this::doPostSortAction);
    }

    void doPostSortAction() {
        branchesList.getSelectionModel().select(0);
        branchesList.scrollTo(0);
        branchesList.requestFocus();
    }

    void setSortOrder(BranchSortOrder sortOrder) {
        BranchSortOrder.setValue(sortOrder);
        setRepo(repo, this::doPostSortAction);
    }

    public void addListener(IBranchListener listener) {
        branchesList.addListener(listener);
    }

    public int getTotal() {
        return branchesList.getItems().size();
    }
}
