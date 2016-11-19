package com.spr.crossgit.branches;

import com.spr.crossgit.ResourceHelper;
import com.spr.crossgit.widgets.ToggleRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

class SortButtonBar {

    private final HBox pane = new HBox();

    SortButtonBar(BranchesPane listener) {

        ToggleGroup tg = new ToggleGroup();

        RadioButton btn1 = new ToggleRadioButton();
        btn1.setUserData(SortOrder.NAME);
        btn1.setGraphic(ResourceHelper.getImage("sort-alpha-16.png"));
        btn1.setToggleGroup(tg);
        btn1.setSelected(SortOrder.isEqualTo(SortOrder.NAME));

        RadioButton btn2 = new ToggleRadioButton();
        btn2.setUserData(SortOrder.NAME_REVERSED);
        btn2.setGraphic(ResourceHelper.getImage("sort-alpha-reversed-16.png"));
        btn2.setToggleGroup(tg);
        btn2.setSelected(SortOrder.isEqualTo(SortOrder.NAME_REVERSED));

        RadioButton btn3 = new ToggleRadioButton();
        btn3.setUserData(SortOrder.DATETIME);
        btn3.setTooltip(new Tooltip("Sort by commit time, oldest first."));
        btn3.setGraphic(ResourceHelper.getImage("sort-numeric-16.png"));
        btn3.setToggleGroup(tg);
        btn3.setSelected(SortOrder.isEqualTo(SortOrder.DATETIME));

        RadioButton btn4 = new ToggleRadioButton();
        btn4.setUserData(SortOrder.DATETIME_REVERSED);
        btn4.setTooltip(new Tooltip("Sort by commit time, most recent first."));
        btn4.setGraphic(ResourceHelper.getImage("sort-numeric-reversed-16.png"));
        btn4.setToggleGroup(tg);
        btn4.setSelected(SortOrder.isEqualTo(SortOrder.DATETIME_REVERSED));

        tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                SortOrder.setValue((SortOrder) newValue.getUserData());
                listener.setSortOrder(SortOrder.getValue());
            }
        });

        pane.getChildren().addAll(btn1, btn2, btn3, btn4);
    }

    void setPrefHeight(double d) {
        pane.setPrefHeight(d);
    }

    public Node node() {
        return pane;
    }

}
