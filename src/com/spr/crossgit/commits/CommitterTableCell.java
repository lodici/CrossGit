package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitCommit;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;

class CommitterTableCell extends TableCell<IGitCommit, IGitCommit> {

    private final VBox graphic = new VBox();
    private final Label dateLabel = new Label();
    private final Label authorLabel = new Label();

    CommitterTableCell() {
        graphic.getChildren().addAll(authorLabel, dateLabel);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        dateLabel.setStyle(null);
    }

    @Override
    protected void updateItem(IGitCommit item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            dateLabel.setText(item.getCommittedOn());
            authorLabel.setText(item.getAuthor());
            setGraphic(graphic);
        }
    }

}
