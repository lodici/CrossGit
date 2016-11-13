package com.spr.crossgit.commits;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.ScreenController;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.FlowPane;
import org.eclipse.jgit.lib.Ref;

class MessageTableCell extends TableCell<GitCommit, GitCommit> {

    private static final Pattern ISSUE_PATTERN = Pattern.compile("\\#+([0-9]+)");

    private GitCommit commit;
    
    @Override
    protected void updateItem(GitCommit commit, boolean empty) {

        super.updateItem(commit, empty);
        this.commit = commit;

        if (empty) {
            setGraphic(null);

        } else {

            final String msg = commit.getMessage();

            final FlowPane flowPane = new FlowPane();
            flowPane.setHgap(4.0);
            flowPane.getChildren().addAll(getBranchNames(commit));
            flowPane.getChildren().addAll(getTagNames(commit));

            final String[] parts = msg.split(" ");
            Stream.of(parts).forEach(part
                    -> flowPane.getChildren().add(isGitHubIssueNumber(part)
                            ? getGitHubIssueLink(part)
                            : new Label(part))
            );

            setGraphic(flowPane);
        }
    }

    private Label getRefLabel(String text) {
        final Label lbl = new Label(text);
        lbl.setStyle("-fx-background-color: " + (commit.isOnActiveBranch() ? "white;" : "#999999;")
                + "-fx-text-fill: #333333;"
                + "-fx-padding: 0 4 0 4;"
                + "-fx-background-radius: 4;"
                + "-fx-font: 12px sans-serif;"
                + "-fx-font-smoothing-type: lcd;"
        );
        return lbl;
    }

    private Label getRefLabel(Ref ref) {
        return getRefLabel(ref.getName().replaceAll("refs/heads/", ""));
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
//            System.out.println(url);
            link.setOnAction((ActionEvent event) -> {
                //openURL(url);
                ScreenController.showWebBrowserScreen(url);
            });
        }
        return link;
    }

    private static void openURL(final String url) {
        try {
            new ProcessBuilder("x-www-browser", url).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Collection<Node> getBranchNames(GitCommit commit) {
        return commit.getHeadRefs().stream()
                .map(ref -> getRefLabel(ref))
                .collect(Collectors.toList());
    }

    private Collection<Node> getTagNames(GitCommit commit) {
        return commit.getTagsList().stream()
                .map(s -> getRefLabel(s.replaceAll("refs/heads/", "")))
                .collect(Collectors.toList());
    }

}
