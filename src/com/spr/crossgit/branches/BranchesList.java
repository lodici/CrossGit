package com.spr.crossgit.branches;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

class BranchesList extends ListView<Ref> {

    private String currentBranch;

    BranchesList() {

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
                final String name = empty ? "" : item.getName().replaceAll("refs/heads/", "");
                setText(name);
                super.setStyle(name.equals(currentBranch)
                            ? "-fx-text-fill: white; -fx-font-weight: bold;"
                            : "-fx-text-fill: #cccccc;"
                );
            }
        });

    }

    private void setCurrentBranch(Repository repo) {
        try {
            this.currentBranch = repo.getBranch();
        } catch (IOException ex) {
            Logger.getLogger(BranchesList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setItems(Repository repo, ObservableList<Ref> branches) {
        super.setItems(branches);
        setCurrentBranch(repo);
    }
}
