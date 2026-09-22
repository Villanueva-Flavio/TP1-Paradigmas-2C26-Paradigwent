package com.fiuba.paradigwent.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuView extends VBox {

    public MainMenuView(View mainView, Stage stage) {
        
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 80, 0, 0));
        
        Button btnPlay = mainView.createMenuButton("START MATCH");
        Button btnFaction = mainView.createMenuButton("FACTIONS");
        Button btnCollection = mainView.createMenuButton("COLLECTION");
        Button btnSettings = mainView.createMenuButton("SETTINGS");
        Button btnExit = mainView.createMenuButton("EXIT");

        btnFaction.setOnAction(e -> mainView.showFactionsMenu());
        btnCollection.setOnAction(e -> mainView.showCollectionMenu());
        btnSettings.setOnAction(e -> mainView.showConfigMenu());
        btnExit.setOnAction(e -> stage.close());

        getChildren().addAll(btnPlay, btnFaction, btnCollection, btnSettings, btnExit);
    }
}