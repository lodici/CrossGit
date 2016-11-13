package com.spr.crossgit.repo.local;

import com.spr.crossgit.screen.MainScreen;
import com.spr.crossgit.ScreenController;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class LocalRepoPane extends VBox {

    private Repository repo;
    private final Hyperlink branchLabel;
    private final Hyperlink statusLink = new Hyperlink();
    private StatusTask statusTask;
    private ExecutorService executor;
    private final RepoComboBox repoCombo;
    private final MainScreen listener;

    public LocalRepoPane(MainScreen listener) {

        this.listener = listener;

        setStyle("-fx-padding: 4;"
                + "-fx-border-style: dotted inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-insets: 2;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #cccccc;"
        );

        this.repoCombo = new RepoComboBox(this);
        
        this.branchLabel = new Hyperlink();
        this.branchLabel.setOnAction((ActionEvent event) -> {
            setStatusLinkText();
        });

        branchLabel.setStyle(
                "-fx-font: 14px sans-serif;"
                + "-fx-padding: 0 0 0 2;"
                + "-fx-alignment: top-left;"
//                + "-fx-border-color: red;"
        );

        setStatusLinkProperties();

        HBox statusHBox = new HBox(branchLabel, statusLink);
        
        getChildren().addAll(repoCombo, statusHBox);

        Platform.runLater(() -> { 
            try { 
                setRepo(repoCombo.getValue());
            } catch (IOException ex) {
                Logger.getLogger(LocalRepoPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void setStatusLinkProperties() {
        statusLink.prefHeightProperty().bind(branchLabel.heightProperty());
        statusLink.setStyle(
                "-fx-font: 12px sans-serif;"
                + "-fx-padding: 0 0 1px 6;"
                + "-fx-text-fill: LightCoral;"
//                + "-fx-border-color: red;"
        );
        statusLink.setOnAction((ActionEvent event) -> {
            ScreenController.showCommitScreen(repo);
        });
    }

    private void setStatusLinkText() {
        if (statusTask != null && statusTask.isRunning()) {
            statusTask.cancel();
        }
        statusLink.setText("...");
        statusTask = new StatusTask(repo);
        statusTask.setOnSucceeded((WorkerStateEvent event) -> {
            statusLink.setText(statusTask.getValue());
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(statusTask);
        executor.shutdown();
    }

    public void setRepo() throws IOException {
        branchLabel.setText("on " + repo.getBranch());
        setStatusLinkText();
    }

    public void setRepo(String repoPath) throws IOException {
        repo = new FileRepositoryBuilder()
                .setGitDir(new File(repoPath, ".git"))
                .setMustExist(true)
                .build();
        listener.setRepo(repo);
        setRepo();
    }
}
