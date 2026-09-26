package com.fiuba.paradigwent.view.menu;

import java.util.List;

import com.fiuba.paradigwent.view.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;


public class InGameConfigMenuView extends VBox {

    private final View mainView;
    private final Stage stage;
    private String currentResolution = "1920x1080";

    public InGameConfigMenuView(View mainView, Stage stage, List<Pair<Integer, Integer>> resolutions) {
    this.mainView = mainView;
    this.stage = stage;

    setPrefWidth(340);
    setMaxWidth(340);
    setSpacing(20);
    setAlignment(Pos.CENTER);
    setPadding(new Insets(20));
    getStyleClass().add("in-game-config-menu");

    Label resolutionLabel = new Label("RESOLUTION");
    resolutionLabel.getStyleClass().add("menu-label");

    ComboBox<String> resolutionCombo = new ComboBox<>();
    resolutionCombo.getItems().addAll(resolutions.stream()
        .map(resolution -> resolution.getKey() + "x" + resolution.getValue())
        .toList());
    resolutionCombo.setValue(currentResolution);
    resolutionCombo.getStyleClass().add("menu-combo-box");
    resolutionCombo.setOnAction(event -> applyResolution(resolutionCombo.getValue()));

    Button fullscreenButton = mainView.createMenuButton(
        stage.isFullScreen() ? "SET TO WINDOWED" : "SET TO FULLSCREEN");
    fullscreenButton.setMaxWidth(320);
    fullscreenButton.setOnAction(event -> stage.setFullScreen(!stage.isFullScreen()));
    stage.fullScreenProperty().addListener((observable, wasFullScreen, isFullScreen) ->
        fullscreenButton.setText(isFullScreen ? "SET TO WINDOWED" : "SET TO FULLSCREEN"));

    Button backButton = mainView.createMenuButton("BACK");
    backButton.setMaxWidth(320);
    backButton.setOnAction(event -> mainView.showGameView());

    Button surrenderButton = mainView.createMenuButton("SURRENDER");
    surrenderButton.setMaxWidth(320);
    surrenderButton.setOnAction(event -> mainView.showMainMenu());

    getChildren().addAll(resolutionLabel, resolutionCombo, fullscreenButton,
        backButton, surrenderButton);
    }

    private void applyResolution(String resolution) {
    if (resolution == null || resolution.equals(currentResolution)) return;

    String[] dimensions = resolution.split("x");
    mainView.applyResolution(stage, Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
    currentResolution = resolution;
    }
}
