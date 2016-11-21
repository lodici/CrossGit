package com.spr.crossgit.tags;

import com.spr.crossgit.api.IGitRepository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.eclipse.jgit.lib.Ref;

class TagsList extends ListView<Ref> {

    private IGitRepository repo;

    TagsList() {

        // branches listview
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ref>() {
            @Override
            public void changed(ObservableValue<? extends Ref> observable, Ref oldValue, Ref newValue) {
                // TO DO
            }
        });

        // each item in the list is a File but display just the file name.
        setCellFactory(lv -> new ListCell<Ref>() {
            @Override
            protected void updateItem(Ref item, boolean empty) {
                super.updateItem(item, empty);
                final String name = empty ? "" : item.getName().replaceAll("refs/tags/", "");
                setText(name);
            }
        });

    }

    void setItems(IGitRepository repo, ObservableList<Ref> tags) {
        this.repo = repo;
        super.setItems(tags);
    }
}
