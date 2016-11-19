package com.spr.crossgit.commits;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.screen.MainScreen;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.eclipse.jgit.lib.Ref;

class CommitsTable extends TableView<GitCommit> {

    CommitsTable(final MainScreen app) {
        setDefaultProperties();
        setSelectListener(app);
        Platform.runLater(() -> requestFocus());
    }

    /**
     * @see https://community.oracle.com/thread/2321823
     */
    void hideHeader() {
        Pane header = (Pane) lookup("TableHeaderRow");
        header.setVisible(false);
        header.setMaxHeight(0);
        header.setMinHeight(0);
        header.setPrefHeight(0);
        setLayoutY(-header.getHeight());
        autosize();
    }

    private void setSelectListener(MainScreen app) {
        getSelectionModel().selectedItemProperty() .addListener(new ChangeListener<GitCommit>() {
            @Override
            public void changed(ObservableValue<? extends GitCommit> observable, GitCommit oldValue, GitCommit newValue) {
                app.onSelectCommit(newValue);
            }
        });        
    }

    void selectFirstRow() {
        Platform.runLater(() -> {
            getSelectionModel().clearAndSelect(0);
        });
    }

    private void setDefaultProperties() {
        setFixedCellSize(44.0);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addComitterColumn();
        addMessageColumn();
    }

    private void addComitterColumn() {
        TableColumn<GitCommit, GitCommit> col = new TableColumn<>("Commits");
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new CommitterTableCell());
        col.setMaxWidth(110);
        col.setMinWidth(110);
        col.setSortable(false);
        col.setEditable(false);
        getColumns().add(col);
    }

    private void addMessageColumn() {
        TableColumn<GitCommit, GitCommit> col = new TableColumn<>();
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new MessageTableCell());
        col.setSortable(false);
        col.setEditable(false);

        // sizes column to table width (but ignores vertical scrollbar).
        // msgCol.prefWidthProperty().bind(commitsTable.widthProperty());

        // sizes column to fit taking in account scrollbar.
        col.setMaxWidth(1f * Integer.MAX_VALUE * 50 ); // 50% width

        getColumns().add(col);
    }

    void select(Ref ref) {
        Optional<GitCommit> commit = getItems().stream()
                .filter(c -> c.isEqualTo(ref))
                .findFirst();
        if (commit.isPresent()) {
            getSelectionModel().select(commit.get());
            scrollTo(commit.get());
        }
    }

}
