package com.spr.crossgit;

import java.awt.Color;
import javafx.scene.Node;

public class Css {

    public static void setBorderColor(Node p, Color c) {
        p.setStyle("-fx-border-color: rgb("
            + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")");
    }

}
