package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitCommit;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;

class MessageTableColumn extends TableColumn<IGitCommit, IGitCommit> {

    MessageTableColumn() {

        setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        setCellFactory(c -> getTableCell());
        setSortable(false);
        setEditable(false);

        // sizes column to table width (but ignores vertical scrollbar).
        // msgCol.prefWidthProperty().bind(commitsTable.widthProperty());

        // sizes column to fit taking in account scrollbar.
        setMaxWidth(1f * Integer.MAX_VALUE * 50 ); // 50% width
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
//                    flowPane.getChildren().addAll(tableView.getBranchNames(commit));
//                    flowPane.getChildren().addAll(getTagNames(commit));
//                    final String[] parts = msg.split(" ");
//                    Stream.of(parts).forEach(part ->
//                        flowPane.getChildren().add(isGitHubIssueNumber(part)
//                            ? getGitHubIssueLink(part)
//                            : new Label(part))
//                    );
                    setGraphic(flowPane);
                }
            }
        };
    }
}
