package com.fiuba.paradigwent.view.menu.modules;

import com.fiuba.paradigwent.view.MainView;
import com.fiuba.paradigwent.view.game.modules.CardRace;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FactionsMenuView extends VBox {

    /**
     * Creates a new FactionsMenuView instance.
     * @param mainView The main View instance.
    **/
    public FactionsMenuView(MainView mainView) {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 80, 0, 0));

        Label lblTitle = new Label("SELECT YOUR RACE");
        lblTitle.getStyleClass().add("menu-label");
        lblTitle.setFont(Font.font(mainView.getFontFamilyName(), 20));

        getChildren().add(lblTitle);
        for (CardRace race : CardRace.values()) {
            Button button = mainView.createMenuButton(race.name());
            button.setOnAction(event -> mainView.selectRace(race));
            getChildren().add(button);
        }
        Button btnBack = mainView.createMenuButton("BACK");
        btnBack.setOnAction(e -> mainView.showMainMenu());
        getChildren().add(btnBack);
    }
}