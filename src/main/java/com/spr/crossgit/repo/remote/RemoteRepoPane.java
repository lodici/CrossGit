package com.spr.crossgit.repo.remote;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Repository;

public class RemoteRepoPane extends VBox {

    private Repository repo;
    private String remoteUrl;
    private final RemoteRepoLink repoLink;
    private final Label statusLabel;
    private RemoteStatusTask statusTask;
    private ExecutorService executor;

    public RemoteRepoPane() {

        setStyle("-fx-padding: 4;"
                + "-fx-border-style: dotted inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-insets: 2;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #cccccc;"
        );
        
        this.repoLink = new RemoteRepoLink(this);

        this.statusLabel = new Label();
        statusLabel.setStyle(
                "-fx-font: 14px sans-serif;"
                + "-fx-padding: 0 0 0 2;"
                + "-fx-alignment: top-left;"
                + "-fx-text-fill: LightCoral;"
        );
        getChildren().addAll(repoLink, statusLabel);
    }

    private void setStatus(Repository repo) throws IOException {
        if (statusTask != null && statusTask.isRunning()) {
            statusTask.cancel();
        }
        statusLabel.setText("...");
        statusTask = new RemoteStatusTask(repo);
        statusTask.setOnSucceeded((WorkerStateEvent event) -> {
            RemoteStatus status = statusTask.getValue();
            if (status != null) {
                statusLabel.setText(!status.getAheadRefs().isEmpty()
                        ? "changes on " + status.getAheadRefs() : "");
            }
        });
        statusTask.setOnFailed((WorkerStateEvent event) -> { 
            final Throwable ex = event.getSource().getException();
            Logger.getLogger(RemoteRepoPane.class.getName())
                    .log(Level.SEVERE, null, ex);
            statusLabel.setText("error: " + ex.getMessage());
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(statusTask);
        executor.shutdown();
    }

    public void setRepo(Repository repo) throws IOException {
        this.repo = repo;
        remoteUrl = repo.getConfig().getString("remote", "origin", "url");
        repoLink.setText(remoteUrl != null ? "-> " + remoteUrl : "");
        statusLabel.setText("");
        if (remoteUrl != null) {
            setStatus(repo);
        }
    }

    void setStatus() {
        try {
            setStatus(repo);
        } catch (IOException ex) {
            Logger.getLogger(RemoteRepoPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
