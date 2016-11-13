package com.spr.crossgit.repo.local;

import com.spr.crossgit.DesktopHelper;
import com.spr.crossgit.Prefs;
import com.spr.crossgit.ScreenController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

class RepoComboBox extends ComboBox<String> {

    private static final int MAX_RECENT_FILES = 10;

    private final RecentList<String> repos = new RecentList<>(MAX_RECENT_FILES);
    private boolean ignoreChangeListener = false;
    private final ContextMenu menu = new ContextMenu();

    RepoComboBox(LocalRepoPane listener) {

        setValue("Click here to open an existing git repository...");
        setItems(getRecentRepos());
        getSelectionModel().select(0);

        setChangeRepoListener(listener);
        setPrimaryMouseClickAction();
        setPopupMenu();
        setShowPopupMenuOnRightClick();
    }

    private void setShowPopupMenuOnRightClick() {
        addEventFilter(MouseEvent.MOUSE_RELEASED, (MouseEvent event) -> {
            if (getItems().isEmpty()) {
                event.consume();
            } else {
                if (event.getButton() == MouseButton.SECONDARY) {
                    event.consume();
                    menu.show(this, event.getScreenX(), event.getScreenY());
                } else {
                    menu.hide();
                }
            }
        });
    }

    private MenuItem getLocalRepoMenuItem() {
        MenuItem item = new MenuItem("Local repository...");
        item.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> { setGitFolder(); });
        });
        return item;
    }

    private MenuItem getExploreMenuItem() {
        MenuItem item = new MenuItem("File explorer");
        item.setOnAction((ActionEvent event) -> {
            try {
                DesktopHelper.openDirectory(getValue());
            } catch (IOException ex) {
//                ScreenController.showWarningMessage( MText.get(_S4, file, ex.getMessage()));
                throw new RuntimeException(ex);
            }
        });
        return item;
    }

    private void setPopupMenu() {
        menu.getItems().clear();
        menu.getItems().addAll(
                getLocalRepoMenuItem(),
                getExploreMenuItem()
        );
        menu.setAutoFix(true);
        menu.setAutoHide(true);
        // Right clicking menu item closes menu.
        menu.addEventFilter(MouseEvent.ANY, (MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
                menu.hide();
            }
        });       
    }

    private void setChangeRepoListener(LocalRepoPane listener) {
        valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!ignoreChangeListener && newValue != null && !newValue.equals(oldValue)) {
                setRepo(listener, newValue);
                doSaveAndSetRepo(new File(newValue));
            }
            }
        });
    }

    private void setPrimaryMouseClickAction() {
        setOnMousePressed((MouseEvent event) -> {
            if (getItems().isEmpty() && event.isPrimaryButtonDown() && event.getClickCount() == 1) {
                event.consume();
                Platform.runLater(() -> {
                    setGitFolder();
                });
            }
        });     
    }

    private void setRepo(LocalRepoPane listener, String newValue) {
        try {
            listener.setRepo(newValue);
        } catch (IOException ex) {
            Logger.getLogger(RepoComboBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isGitFolder(File folder) {
        return folder == null ? false : new File(folder, ".git").exists();
    }

    private boolean setGitFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select folder containing git repository.");
        final File gitFolder = new File(getValue());
        if (gitFolder.exists()) {
            chooser.setInitialDirectory(gitFolder);
        }
        final File folder = chooser.showDialog(ScreenController.getStage());
        if (isGitFolder(folder)) {
            doSaveAndSetRepo(folder);
        }
        return folder != null;
    }

    private void doSaveAndSetRepo(File folder) {

        repos.addToTop(folder.getAbsolutePath());

        int idx = 0;
        for (String repo : repos) {
            Prefs.userPrefs.put("recent.repo." + idx, repo);
            idx++;
        }

        // this block is important to prevent IndexOutOfBoundsException.
        ignoreChangeListener = true;
        Platform.runLater(() -> {
            setItems(getRecentRepos());
            ignoreChangeListener = false;
            getSelectionModel().select(0);
        });
    }

    private ObservableList<String> getRecentRepos() {

        repos.clear();
        for (int i = 0; i < MAX_RECENT_FILES; i++) {
            String val = Prefs.userPrefs.get("recent.repo." + i, "");
            if (!val.equals("")) {
                repos.add(val);
            } else {
                break;
            }
        }

        final List<String> list = new ArrayList<>();
        repos.forEach(list::add);
        return FXCollections.observableArrayList(list);
    }



}
