package com.spr.crossgit;

import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.screen.MainScreen;
import com.spr.crossgit.screen.StartScreen;
import com.spr.crossgit.screen.WebBrowserScreen;
import com.spr.crossgit.screen.commit.CommitScreen;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public final class ScreenController {

    private static final String TITLE = "CrossGit 0.20";

    private static final Stack<IScreen> screens = new Stack<>();
    private static Stage mainStage;

    private static void showScreen(IScreen screen) {
        screens.push(screen);
        if (mainStage.getScene() == null) {
            setMainStageScene(screen);
        } else {
            mainStage.getScene().setRoot(screen.getRoot());
        }
    }

    private static void setMainStageScene(IScreen screen) {
        final Scene scene;
        scene = new Scene(screen.getRoot(), 800, 600, Color.BLACK);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(MainApp.class.getResource("/resources/styles/ClientApp.css").toExternalForm());
        mainStage.setScene(scene);
    }

    private static void doCloseActiveScreen() {
        final IScreen activeScreen = screens.pop();
        showScreen(screens.pop());
    }

    private static boolean requestConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation required");
        alert.setHeaderText("Exit to desktop?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    private static void closeActiveScreenOnEscape() {
        if (screens.size() > 1) {
            doCloseActiveScreen();
        } else {
            if (requestConfirmation()) {
                mainStage.close();
            }
        }
    }

    public static void closeActiveScreen() {
        if (screens.size() > 1) {
            doCloseActiveScreen();
        } else {
            mainStage.close();
        }
    }

    static void setStage(Stage aStage) {
        mainStage = aStage;
        mainStage.setTitle(TITLE);
        mainStage.setMaximized(!Prefs.isDevMode);
        mainStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
            closeActiveScreen();
        });
        mainStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                Platform.runLater(() -> closeActiveScreenOnEscape());
            }
        });
        showMainScreen();
//        showStartScreen();
        mainStage.show();
    }

    public static Stage getStage() {
        return mainStage;
    }

    public static void showStartScreen() {
        showScreen(new StartScreen());
    }

    public static void showMainScreen() {
        showScreen(new MainScreen());
    }

    public static void showWebBrowserScreen(String url) {
        showScreen(new WebBrowserScreen(url));
    }

    public static void showCommitScreen(IGitRepository repo) {
        showScreen(new CommitScreen(repo));
    }

    private ScreenController() { }

}
