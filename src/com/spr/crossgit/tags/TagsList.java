package com.spr.crossgit.tags;

import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.api.IGitTag;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

class TagsList extends ListView<IGitTag> {

    private IGitRepository repo;

    TagsList() {

        // branches listview
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<IGitTag>() {
            @Override
            public void changed(ObservableValue<? extends IGitTag> observable, IGitTag oldValue, IGitTag newValue) {
                // TO DO
            }
        });

        // each item in the list is a File but display just the file name.
        setCellFactory(lv -> new ListCell<IGitTag>() {
            @Override
            protected void updateItem(IGitTag tag, boolean empty) {
                super.updateItem(tag, empty);
                setText(empty ? "" : tag.getName());
            }
        });

    }

    void setItems(IGitRepository repo, ObservableList<IGitTag> tags) {
        this.repo = repo;
        super.setItems(tags);
    }
}
