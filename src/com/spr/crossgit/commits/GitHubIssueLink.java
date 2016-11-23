package com.spr.crossgit.commits;

import com.spr.crossgit.ScreenController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

class GitHubIssueLink extends Hyperlink {

    private static final Pattern ISSUE_PATTERN = Pattern.compile("\\#+([0-9]+)");

    GitHubIssueLink(String text) {

        setStyle(""
//                + "-fx-background-color: #cccccc;"
//                + "-fx-text-fill: lightblue;"
//                + "-fx-padding: 0;"
//                + "-fx-background-radius: 2;"
                + "-fx-font: 14px consolas;"
                + "-fx-font-smoothing-type: lcd;"
        );

        Matcher m = ISSUE_PATTERN.matcher(text);
        if (m.find()) {
            final String url = "https://github.com/magarena/magarena/issues/" + m.group(1);
            setOnAction((ActionEvent event) -> {
                //openURL(url);
                ScreenController.showWebBrowserScreen(url);
            });
        }
    }
}
