package com.spr.crossgit.screen.commit;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.eclipse.jgit.lib.Repository;

class ChangeSetTable extends TableView<String> {

    private final Repository repo;
    private final CommitScreen listener;

    ChangeSetTable(Repository repo, CommitScreen listener) {
        this.repo = repo;
        this.listener = listener;
        setDefaultProperties();
        setSelectListener();
        Platform.runLater(() -> {
            requestFocus();
        });
    }

    void selectFirstRow() {
        Platform.runLater(() -> {
            getSelectionModel().clearAndSelect(0);
        });
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

    private void onFileSelected(String path) {
        listener.showFileDiff(path);
    }

    private void setSelectListener() {
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                onFileSelected(newValue);
            }
        });
    }

    private void setDefaultProperties() {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addFileColumn();
    }

    private void addFileColumn() {
        TableColumn<String, String> col = new TableColumn<>("File");
        col.setMaxWidth(1f * Integer.MAX_VALUE * 50); // 50% width
        col.setCellValueFactory(new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<String, String> p) {
                return new ReadOnlyStringWrapper(p.getValue());
            }
        });
        col.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        String csf = getItems().get(getIndex());
                        setText(item);
//                        setStyle(csf.getStyle());
                    } else {
                        setText("");
                        setStyle(null);
                    }
                }
            };
        });
        getColumns().add(col);
    }
}
