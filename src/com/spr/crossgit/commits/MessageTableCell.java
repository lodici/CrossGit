package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitRepository;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.FlowPane;

class MessageTableCell extends TableCell<IGitCommit, IGitCommit> {

    private final IGitRepository repo;

    MessageTableCell(IGitRepository repo) {
        this.repo = repo;
    }

    private Collection<Node> getBranchLabels(IGitCommit commit) {
        return repo.getBranchHeadsAt(commit).stream()
            .map(branch -> new RefLabel(branch))
            .collect(Collectors.toList());
    }

    private Collection<Node> getTagLabels(IGitCommit commit) {
        return repo.getTagsAt(commit).stream()
            .map(tag -> new RefLabel(tag))
            .collect(Collectors.toList());
    }

    private boolean isGitHubIssue(String s) {
        return s.contains("#");
    }

    @Override
    protected void updateItem(IGitCommit commit, boolean empty) {
        super.updateItem(commit, empty);
        if (empty) {
            setGraphic(null);
        } else {
            final FlowPane flowPane = new FlowPane();
            flowPane.setHgap(4.0);
            flowPane.getChildren().addAll(getBranchLabels(commit));
            flowPane.getChildren().addAll(getTagLabels(commit));
            final String[] parts = commit.getMessage().split(" ");
            Stream.of(parts).forEach(part ->
                flowPane.getChildren().add(isGitHubIssue(part)
                    ? new GitHubIssueLink(part)
                    : new Label(part))
            );
            setGraphic(flowPane);
        }
    }
}
