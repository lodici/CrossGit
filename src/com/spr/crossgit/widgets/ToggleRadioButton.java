package com.spr.crossgit.widgets;

import javafx.scene.control.RadioButton;

/**
 * Looks like a ToggleButton but acts like a RadioButton in that
 * it forces the selection of at least one button in a group.
 */
public class ToggleRadioButton extends RadioButton {

    public ToggleRadioButton() {
        setStyle();
    }

    public ToggleRadioButton(String text) {
        super(text);
        setStyle();
    }

    private void setStyle() {
        getStyleClass().remove("radio-button");
        getStyleClass().add("toggle-button");
    }
}
