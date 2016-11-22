package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitBranch;
import javafx.scene.control.Label;

class RefLabel extends Label {

    RefLabel(String text) {
        super(text);
        setStyle("-fx-background-color: #999999;" // + (repo.isCurrentBranchHead(commit) ? "white;" : "#999999;")
                + "-fx-text-fill: #333333;"
                + "-fx-padding: 0 4 0 4;"
                + "-fx-background-radius: 4;"
                + "-fx-font: 12px sans-serif;"
                + "-fx-font-smoothing-type: lcd;"
        );
    }

    RefLabel(IGitBranch branch) {
        this(branch.getName().replaceAll("refs/heads/", ""));
    }
}
