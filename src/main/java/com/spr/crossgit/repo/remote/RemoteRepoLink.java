package com.spr.crossgit.repo.remote;

import com.spr.crossgit.ScreenController;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;

class RemoteRepoLink extends Hyperlink {

    RemoteRepoLink(RemoteRepoPane listener) {
        setPopupMenu(listener);
        setMouseClickAction();
    }

    private void setMouseClickAction() {
        setOnAction((ActionEvent event) -> {
            String url = getText().replaceAll("->", "").trim();
            ScreenController.showWebBrowserScreen(url);
        });
    }

    private void setPopupMenu(RemoteRepoPane listener) {
        ContextMenu menu = new ContextMenu();
        MenuItem item = new MenuItem("Check for changes");
        item.setOnAction((ActionEvent event) -> {
            listener.setStatus();
        });
        menu.getItems().add(item);
        setContextMenu(menu);
    }

}
