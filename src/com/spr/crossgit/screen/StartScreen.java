package com.spr.crossgit.screen;

import com.spr.crossgit.IScreen;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class StartScreen implements IScreen {

    private final BorderPane root = new BorderPane();

    @Override
    public Parent getRoot() {
        return root;
    }

}
