package com.spr.crossgit.commits;

import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitCommit;
import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.screen.MainScreen;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

class CommitsTable extends TableView<IGitCommit> {

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
        getSelectionModel().selectedItemProperty() .addListener(new ChangeListener<IGitCommit>() {
            @Override
            public void changed(ObservableValue<? extends IGitCommit> observable, IGitCommit oldValue, IGitCommit newValue) {
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
    }

    private void addComitterColumn() {
        TableColumn<IGitCommit, IGitCommit> col = new TableColumn<>("Commits");
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new CommitterTableCell());
        col.setMaxWidth(110);
        col.setMinWidth(110);
        col.setSortable(false);
        col.setEditable(false);
        getColumns().add(col);
    }

    private void addMessageColumn(IGitRepository repo) {
        TableColumn<IGitCommit, IGitCommit> col = new TableColumn<>();
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new MessageTableCell(repo));
        col.setSortable(false);
        col.setEditable(false);

        // sizes column to table width (but ignores vertical scrollbar).
        // msgCol.prefWidthProperty().bind(commitsTable.widthProperty());

        // sizes column to fit taking in account scrollbar.
        col.setMaxWidth(1f * Integer.MAX_VALUE * 50 ); // 50% width

        getColumns().add(col);
    }

    private VirtualFlow<?> loadVirtualFlow() {
        return (VirtualFlow<?>) ((TableViewSkin<?>) getSkin()).getChildren().get(1);
    }

    private int getNumberOfVisibleRows() {
        VirtualFlow<?> vf = loadVirtualFlow();
        return vf.getLastVisibleCell().getIndex() - vf.getFirstVisibleCell().getIndex();
    }

    void select(IGitBranch branch) {
        Optional<IGitCommit> commit = getItems().stream()
                .filter(c -> c.isHeadOf(branch))
                .findFirst();
        if (commit.isPresent()) {
            getSelectionModel().select(commit.get());
            int visibleRows = getNumberOfVisibleRows();
            scrollTo(getSelectionModel().getSelectedIndex() - (visibleRows / 2));
        }
    }

    private void setTableColumns(IGitRepository repo) {
        addComitterColumn();
        addMessageColumn(repo);
    }

    void setItems(ObservableList<IGitCommit> commits, IGitRepository repo) {
        if (getColumns().isEmpty()) {
            setTableColumns(repo);
        }
        setItems(commits);
    }

}
