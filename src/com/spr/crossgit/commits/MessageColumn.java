package com.spr.crossgit.commits;

import com.spr.crossgit.ScreenController;
import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitRepository;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;

class MessageColumn extends TableColumn<IGitCommit, IGitCommit> {

    private static final Pattern ISSUE_PATTERN = Pattern.compile("\\#+([0-9]+)");

    private final IGitRepository repo;

    MessageColumn(IGitRepository repo) {

        this.repo = repo;

        setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        setCellFactory(c -> getTableCell());
        setSortable(false);
        setEditable(false);

        // sizes column to table width (but ignores vertical scrollbar).
        // msgCol.prefWidthProperty().bind(commitsTable.widthProperty());

        // sizes column to fit taking in account scrollbar.
        setMaxWidth(1f * Integer.MAX_VALUE * 50 ); // 50% width
    }

    private Collection<Node> getBranchLabels(IGitCommit commit) {
        return repo.getBranchHeadsAt(commit).stream()
                .map(b -> new RefLabel(b))
                .collect(Collectors.toList());
    }

    private Collection<Node> getTagLabels(IGitCommit commit) {
        return repo.getTagsAt(commit).stream()
                .map(t -> new RefLabel(t))
                .collect(Collectors.toList());
    }

    private boolean isGitHubIssueNumber(String s) {
        return s.contains("#");
    }

    private Hyperlink getGitHubIssueLink(String issue) {
        Hyperlink link = new Hyperlink(issue);
        link.setStyle(""
//                + "-fx-background-color: #cccccc;"
//                + "-fx-text-fill: lightblue;"
//                + "-fx-padding: 0;"
//                + "-fx-background-radius: 2;"
                + "-fx-font: 14px consolas;"
                + "-fx-font-smoothing-type: lcd;"
        );
        Matcher m = ISSUE_PATTERN.matcher(issue);
        if (m.find()) {
            final String url = "https://github.com/magarena/magarena/issues/"
                    + m.group(1);
            link.setOnAction((ActionEvent event) -> {
                //openURL(url);
                ScreenController.showWebBrowserScreen(url);
            });
        }
        return link;
    }

    private TableCell<IGitCommit, IGitCommit> getTableCell() {
        return new TableCell<IGitCommit, IGitCommit>() {
            @Override
            protected void updateItem(IGitCommit commit, boolean empty) {
                super.updateItem(commit, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    final String msg = commit.getMessage();
                    final FlowPane flowPane = new FlowPane();
                    flowPane.setHgap(4.0);
                    flowPane.getChildren().addAll(getBranchLabels(commit));
                    flowPane.getChildren().addAll(getTagLabels(commit));
                    final String[] parts = msg.split(" ");
                    Stream.of(parts).forEach(part ->
                        flowPane.getChildren().add(isGitHubIssueNumber(part)
                            ? getGitHubIssueLink(part)
                            : new Label(part))
                    );
                    setGraphic(flowPane);
                }
            }
        };
    }
}
