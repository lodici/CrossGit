package com.spr.crossgit;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        ScreenController.setStage(primaryStage);
    }

    // makes a difference on linux at least.
    private static void setBetterFontQuality() {
        System.setProperty("prism.lcdtext", "true");
        System.setProperty("prism.text", "t2k");
    }

    private static void setSingleLineLogger() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setSingleLineLogger();
        setBetterFontQuality();
        launch(args);
    }
}
