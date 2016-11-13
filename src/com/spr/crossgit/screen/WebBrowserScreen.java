package com.spr.crossgit.screen;

import com.spr.crossgit.IScreen;
import com.spr.crossgit.ScreenController;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebBrowserScreen implements IScreen {

    private final BorderPane root = new BorderPane();

    public WebBrowserScreen(String url) {

        Button btn = new Button("Close Screen");
        btn.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                ScreenController.closeActiveScreen();
            });
        });
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            System.out.println(newValue);
        });
        Platform.runLater(() -> webView.getEngine().load(url));

        root.setTop(btn);
        root.setCenter(webView);
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
