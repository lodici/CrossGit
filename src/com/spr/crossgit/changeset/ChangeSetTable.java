package com.spr.crossgit.changeset;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

class ChangeSetTable extends TableView<ChangeSetFile> {

    ChangeSetTable() {
        setDefaultProperties();
        setSelectListener();
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

    private void setSelectListener() {
        getSelectionModel().selectedItemProperty() .addListener(new ChangeListener<ChangeSetFile>() {
            @Override
            public void changed(ObservableValue<? extends ChangeSetFile> observable, ChangeSetFile oldValue, ChangeSetFile newValue) {
                System.out.println(newValue);
            }
        });        
    }

    private void setDefaultProperties() {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addFileColumn();
    }

    private void addFileColumn() {
        TableColumn<ChangeSetFile, String> col = new TableColumn<>("File");
        col.setMaxWidth(1f * Integer.MAX_VALUE * 50 ); // 50% width
        col.setCellValueFactory(new PropertyValueFactory<>("text"));
        col.setCellFactory(column -> {
            return new TableCell<ChangeSetFile, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        ChangeSetFile csf = getItems().get(getIndex());
                        setText(item);
                        setStyle(csf.getStyle());
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
