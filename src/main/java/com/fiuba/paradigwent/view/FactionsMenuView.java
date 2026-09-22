package com.fiuba.paradigwent.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FactionsMenuView extends VBox {

    public FactionsMenuView(View mainView) {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 80, 0, 0));

        Label lblTitle = new Label("FACTIONS (Soon TM)");
        lblTitle.getStyleClass().add("menu-label");
        lblTitle.setFont(Font.font(mainView.getFontFamilyName(), 20));

        Button btnBack = mainView.createMenuButton("BACK");
        btnBack.setOnAction(e -> mainView.showMainMenu());

        getChildren().addAll(lblTitle, btnBack);
    }
}