package com.spr.crossgit.branches;

import com.spr.crossgit.screen.MainScreen;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final BranchesList branchesList;
    private BranchesTask task;
    private ExecutorService executor;
    private final MainScreen app;

    public BranchesPane(MainScreen app) {
        this.app = app;
        this.branchesList = new BranchesList();
        VBox.setVgrow(branchesList, Priority.ALWAYS);
        pane.getChildren().addAll(branchesList);
    }

    public Node node() {
        return pane;
    }

    public void setRepo(Repository repo) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        branchesList.setItems(FXCollections.emptyObservableList());
        task = new BranchesTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            final BranchesInfo branchInfo = task.getValue();
            final ObservableList<Ref> branches = branchInfo.getRefsList();
            branchesList.setItems(repo, branches);
            app.setBranches(branchInfo);
            if (!branches.isEmpty()) {
                try {
                    final Ref current = repo.findRef(repo.getFullBranch());
                    branchesList.getSelectionModel().select(current);
                    branchesList.scrollTo(current);
                } catch (IOException ex) {
                    Logger.getLogger(BranchesPane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }
}
