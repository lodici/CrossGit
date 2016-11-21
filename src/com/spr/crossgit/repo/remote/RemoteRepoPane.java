package com.spr.crossgit.repo.remote;

import com.spr.crossgit.api.IGitRepository;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RemoteRepoPane extends VBox {

    private IGitRepository repo;
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

    private void setStatus(IGitRepository repo) throws IOException {

        // TODO: need to fetch first before this becomes meaningful.
        if (true) return;

        if (statusTask != null && statusTask.isRunning()) {
            statusTask.cancel();
        }
        statusLabel.setText("...");
        statusTask = new RemoteStatusTask(repo);
        statusTask.setOnSucceeded((WorkerStateEvent event) -> {
            RemoteStatus status = statusTask.getValue();
            if (status != null && status.isChanged()) {
//                statusLabel.setText(!status.getAheadRefs().isEmpty()
//                        ? "changes on " + status.getAheadRefs() : "");
                statusLabel.setText("ahead=" + status.commitsAhead
                        + ", behind=" + status.commitsBehind);
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

    void setStatus() {
        try {
            setStatus(repo);
        } catch (IOException ex) {
            Logger.getLogger(RemoteRepoPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setRepo(IGitRepository r) throws IOException {
        this.repo = r;
        remoteUrl = repo.getRemoteUrl();
        repoLink.setText(remoteUrl != null ? "-> " + remoteUrl : "");
        statusLabel.setText("");
        if (remoteUrl != null) {
            setStatus();
        }
    }
}
