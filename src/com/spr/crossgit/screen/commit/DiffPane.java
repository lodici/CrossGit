package com.spr.crossgit.screen.commit;

import com.spr.crossgit.api.IGitRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DiffPane extends VBox {

    private final IGitRepository repo;
    private final TextArea textArea = new TextArea();
    private ExecutorService executor;
    private DiffTask task;

    public DiffPane(IGitRepository repo) {
        this.repo = repo;
        textArea.setEditable(false);
        setVgrow(textArea, Priority.ALWAYS);
        getChildren().addAll(textArea);
    }

    public void showFileDiff(String filePath) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        task = new DiffTask(repo, filePath);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            textArea.setText(task.getValue());
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
