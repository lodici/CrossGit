package com.spr.crossgit.commits;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.screen.MainScreen;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.eclipse.jgit.lib.Ref;

class CommitsTable extends TableView<GitCommit> {

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
        getSelectionModel().selectedItemProperty() .addListener(new ChangeListener<GitCommit>() {
            @Override
            public void changed(ObservableValue<? extends GitCommit> observable, GitCommit oldValue, GitCommit newValue) {
                app.onSelectCommit(newValue);
            }
        });        
    }

    void selectFirstRow() {
        Platform.runLater(() -> {
            getSelectionModel().clearAndSelect(0);
        });
    }
    
    private void setRowContextMenu(TableRow<GitCommit> row, GitCommit commit) {
        final ContextMenu menu = new ContextMenu();
        final List<Ref> branches = commit.getHeadRefs();
        if (!branches.isEmpty()) {
            if (branches.size() == 1) {
                final MenuItem item = new MenuItem("Delete branch");
                item.setOnAction((ActionEvent event) -> {
                    Stage myDialog = new Stage();
                    myDialog.initModality(Modality.APPLICATION_MODAL);
                    Scene myDialogScene = new Scene(VBoxBuilder.create()
                            .prefWidth(640)
                            .prefHeight(480)
                            .children(new Text("Hello! it's My Dialog."))
                            .alignment(Pos.CENTER)
                            .padding(new Insets(10))
                            .build());
                    myDialog.setScene(myDialogScene);
                    myDialog.show();
                });
                menu.getItems().add(item);
            } else {
                final Menu subMenu = new Menu("Delete branch");
                for (Ref ref : branches) {
                    final MenuItem item = new MenuItem(ref.getName().replaceAll("refs/heads/", ""));
                    subMenu.getItems().add(item);
                }
                menu.getItems().add(subMenu);
            }
        }
        row.setContextMenu(menu);
//        try (Git git = new Git(repo)) {
//            DeleteBranchCommand cmd = git.branchDelete();
//            cmd.setBranchNames(branchnames);
//        }
    }

    private void setRowFactory() {
        setRowFactory(new Callback<TableView<GitCommit>, TableRow<GitCommit>>() {
            @Override
            public TableRow<GitCommit> call(TableView<GitCommit> tv) {
                return new TableRow<GitCommit>() {
                    @Override
                    protected void updateItem(GitCommit item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setRowContextMenu(this, item);
                        }
                    }
                };
            }
        });
    }

    private void setDefaultProperties() {
        setFixedCellSize(44.0);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addComitterColumn();
        addMessageColumn();
        setRowFactory();
    }

    private void addComitterColumn() {
        TableColumn<GitCommit, GitCommit> col = new TableColumn<>("Commits");
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new CommitterTableCell());
        col.setMaxWidth(110);
        col.setMinWidth(110);
        col.setSortable(false);
        col.setEditable(false);
        getColumns().add(col);
    }

    private void addMessageColumn() {
        TableColumn<GitCommit, GitCommit> col = new TableColumn<>();
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        col.setCellFactory(c -> new MessageTableCell());
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

    void select(Ref ref) {
        Optional<GitCommit> commit = getItems().stream()
                .filter(c -> c.isEqualTo(ref))
                .findFirst();
        if (commit.isPresent()) {
            getSelectionModel().select(commit.get());
            int visibleRows = getNumberOfVisibleRows();
            scrollTo(getSelectionModel().getSelectedIndex() - (visibleRows / 2));
        }
    }
    
}
