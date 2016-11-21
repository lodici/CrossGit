package com.spr.crossgit.commits;

import com.spr.crossgit.ScreenController;
import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitTag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.FlowPane;

class MessageTableCell extends TableCell<IGitCommit, IGitCommit> {

    private static final Pattern ISSUE_PATTERN = Pattern.compile("\\#+([0-9]+)");

//    private IGitCommit commit;
//    private IGitRepository repo;

//    public MessageTableCell(final IGitRepository repo) {
//        this.repo = repo;
//    }

    @Override
    protected void updateItem(IGitCommit commit, boolean empty) {

        super.updateItem(commit, empty);
//        this.commit = commit;

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
        lbl.setStyle("-fx-background-color: #999999;" // + (repo.isCurrentBranchHead(commit) ? "white;" : "#999999;")
                + "-fx-text-fill: #333333;"
                + "-fx-padding: 0 4 0 4;"
                + "-fx-background-radius: 4;"
                + "-fx-font: 12px sans-serif;"
                + "-fx-font-smoothing-type: lcd;"
        );
        return lbl;
    }

    private Label getRefLabel(IGitBranch branch) {
        return getRefLabel(branch.getName().replaceAll("refs/heads/", ""));
    }

    private Label getRefLabel(IGitTag tag) {
        return getRefLabel(tag.getName().replaceAll("refs/tags/", ""));
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

    private Collection<Node> getBranchNames(IGitCommit commit) {
        return new ArrayList<>();
//        return repo.getBranchHeadsAt(commit).stream()
//                .map(b -> getRefLabel(b))
//                .collect(Collectors.toList());
    }

    private Collection<Node> getTagNames(IGitCommit commit) {
        return new ArrayList<>();
//        return repo.getTagsAt(commit).stream()
//                .map(t -> getRefLabel(t))
//                .collect(Collectors.toList());
    }

}
