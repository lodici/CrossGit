package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitRepository;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;

class MessageTableColumn extends TableColumn<IGitCommit, IGitCommit> {

    MessageTableColumn(IGitRepository repo) {

        setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        setCellFactory(c -> new MessageTableCell(repo));
        setSortable(false);
        setEditable(false);

        // sizes column to table width (but ignores vertical scrollbar).
        // msgCol.prefWidthProperty().bind(commitsTable.widthProperty());

        // sizes column to fit taking in account scrollbar.
        setMaxWidth(1f * Integer.MAX_VALUE * 50 ); // 50% width
    }
}
