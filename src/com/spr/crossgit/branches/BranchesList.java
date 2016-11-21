package com.spr.crossgit.branches;

import com.spr.crossgit.IBranchListener;
import com.spr.crossgit.api.IGitBranch;
import com.spr.crossgit.api.IGitRepository;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

class BranchesList extends ListView<IGitBranch> {

    private final List<IBranchListener> listeners = new ArrayList<>();

    private void setChangeListener() {
        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<IGitBranch>() {
            @Override
            public void changed(ObservableValue<? extends IGitBranch> observable, IGitBranch oldValue, IGitBranch newValue) {
                if (newValue != null && !newValue.equals(oldValue)) {
                    listeners.forEach(l -> l.setBranch(newValue));
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

    private void setCellFactory(IGitRepository repo) {
        setCellFactory(lv -> new ListCell<IGitBranch>() {
            @Override
            protected void updateItem(IGitBranch branch, boolean empty) {
                super.updateItem(branch, empty);
                if (!empty) {
                    setText(branch.getName());
                    super.setStyle(repo.isCurrentBranch(branch)
                        ? "-fx-text-fill: white; -fx-font-weight: bold;"
                        : "-fx-text-fill: #cccccc;"
                    );
                } else {
                    setText("");
                }
            }
        });
    }

    void setItems(ObservableList<IGitBranch> branches, IGitRepository repo) {
        setCellFactory(repo);
        setItems(branches);
    }
}
