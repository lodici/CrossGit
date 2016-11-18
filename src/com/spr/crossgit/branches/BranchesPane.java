package com.spr.crossgit.branches;

import com.spr.crossgit.screen.MainScreen;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class BranchesPane {

    private final VBox pane = new VBox();
    private final SortButtonBar sortBar;
    private final BranchesList branchesList;
    private BranchesTask task;
    private ExecutorService executor;
    private final MainScreen app;
    private Repository repo;
    private BranchesInfo branchInfo;
    private SortOrder sortOrder = SortOrder.NAME;

    public BranchesPane(MainScreen app) {
        this.app = app;
        branchesList = new BranchesList();
        sortBar = new SortButtonBar(this);
        sortBar.setPrefHeight(28.0);
        VBox.setVgrow(branchesList, Priority.ALWAYS);
        pane.getChildren().addAll(branchesList, sortBar.node());
    }

    public Node node() {
        return pane;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        branchesList.setItems(FXCollections.emptyObservableList());
        task = new BranchesTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            branchInfo = task.getValue();
            final ObservableList<Ref> branches = branchInfo.getRefsList(sortOrder);
            branchesList.setItems(repo, branches);
            app.setBranches(branchInfo);
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }

    void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        branchesList.setItems(repo, branchInfo.getRefsList(sortOrder));
        branchesList.scrollTo(0);
        branchesList.requestFocus();
    }
}
