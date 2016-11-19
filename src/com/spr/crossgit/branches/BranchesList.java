package com.spr.crossgit.branches;

import com.spr.crossgit.IBranchListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private final List<IBranchListener> listeners = new ArrayList<>();

    BranchesList() {

        // each item in the list is a Ref but display just the branch name.
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

    private void setChangeListener() {
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ref>() {
            @Override
            public void changed(ObservableValue<? extends Ref> observable, Ref oldValue, Ref newValue) {
                if (newValue != null && !newValue.equals(oldValue)) {
                    listeners.forEach(l -> l.setBranchRef(newValue));
                }
            }
        });
    }

    public void addListener(IBranchListener listener) {
        if (listeners.isEmpty()) {
            setChangeListener();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
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
