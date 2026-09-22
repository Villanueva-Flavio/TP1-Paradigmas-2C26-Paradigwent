package com.fiuba.paradigwent.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.List;

public class ConfigMenuView extends VBox {

    private final View mainView;
    private final Stage stage;
    private String currentResolution = "1920x1080";
    private Timeline confirmationTimeline;
    private int countdownSeconds = 10;
    private boolean isUpdatingCombo = false;

    public ConfigMenuView(View mainView, Stage stage, List<Pair<Integer, Integer>> resolutions) {
        this.mainView = mainView;
        this.stage = stage;

        configureLayout();

        Label lblResolution = createResolutionLabel();
        ComboBox<String> comboResolution = createResolutionComboBox(resolutions);
        Button btnFullscreen = createFullscreenButton();
        Button btnBack = createBackButton();

        getChildren().addAll(lblResolution, comboResolution, btnFullscreen, btnBack);
    }

    private void configureLayout() {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 80, 0, 0));
    }

    private Label createResolutionLabel() {
        Label lblResolution = new Label("RESOLUTION");
        lblResolution.getStyleClass().add("menu-label");
        return lblResolution;
    }

    private ComboBox<String> createResolutionComboBox(List<Pair<Integer, Integer>> resolutions) {
        ComboBox<String> comboResolution = new ComboBox<>();
        comboResolution.getItems().addAll(resolutions.stream().map(res -> res.getKey() + "x" + res.getValue()).toList());
        comboResolution.setValue(currentResolution);
        comboResolution.getStyleClass().add("menu-combo-box");
        comboResolution.setOnAction(e -> handleResolutionChange(comboResolution));
        return comboResolution;
    }

    private void handleResolutionChange(ComboBox<String> comboResolution) {
        if (isUpdatingCombo) return;

        String selected = comboResolution.getValue();
        if (selected != null && !selected.equals(currentResolution)) {
            String previousRes = currentResolution;
            applyResolution(selected);
            showResolutionConfirmationOverlay(comboResolution, previousRes, selected);
        }
    }

    private Button createFullscreenButton() {
        Button btnFullscreen = mainView.createMenuButton(stage.isFullScreen() ? "SET TO WINDOWED" : "SET TO FULLSCREEN");
        btnFullscreen.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));

        stage.fullScreenProperty().addListener((obs, wasFS, isFS) -> {
            btnFullscreen.setText(isFS ? "SET TO WINDOWED" : "SET TO FULLSCREEN");
        });

        return btnFullscreen;
    }

    private Button createBackButton() {
        Button btnBack = mainView.createMenuButton("BACK");
        btnBack.setOnAction(e -> mainView.showMainMenu());
        return btnBack;
    }

    private void applyResolution(String resolutionStr) {
        stage.setFullScreen(false);
        String[] dimensions = resolutionStr.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }

    private void showResolutionConfirmationOverlay(ComboBox<String> comboResolution, String previousRes, String newRes) {
        countdownSeconds = 10;
        Label lblTimer = new Label("Rolling back in " + countdownSeconds + " seconds...");
        lblTimer.getStyleClass().add("dialog-timer");

        StackPane overlay = createConfirmationOverlay(lblTimer, comboResolution, previousRes, newRes);
        mainView.getRoot().getChildren().add(overlay);

        startConfirmationTimer(overlay, lblTimer, comboResolution, previousRes);
    }

    private StackPane createConfirmationOverlay(Label lblTimer, ComboBox<String> combo, String previousRes, String newRes) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("dialog-overlay");

        Label lblTitle = new Label("PRESERVE CONFIG?");
        lblTitle.getStyleClass().add("dialog-title");

        HBox buttonContainer = createOverlayButtons(overlay, combo, previousRes, newRes);

        VBox dialogBox = new VBox(15, lblTitle, lblTimer, buttonContainer);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setMaxSize(460, 240);
        dialogBox.getStyleClass().add("dialog-box");

        overlay.getChildren().add(dialogBox);
        return overlay;
    }

    private HBox createOverlayButtons(StackPane overlay, ComboBox<String> combo, String previousRes, String newRes) {
        Button btnConfirm = mainView.createMenuButton("CONFIRM");
        btnConfirm.getStyleClass().add("menu-button-dialog");
        btnConfirm.setOnAction(e -> {
            if (confirmationTimeline != null) confirmationTimeline.stop();
            currentResolution = newRes;
            mainView.getRoot().getChildren().remove(overlay);
        });

        Button btnRevert = mainView.createMenuButton("REVERT");
        btnRevert.getStyleClass().add("menu-button-dialog");
        btnRevert.setOnAction(e -> revertResolution(overlay, combo, previousRes));

        HBox container = new HBox(15, btnConfirm, btnRevert);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private void startConfirmationTimer(StackPane overlay, Label lblTimer, ComboBox<String> combo, String previousRes) {
        confirmationTimeline = new Timeline();
        confirmationTimeline.setCycleCount(10);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            countdownSeconds--;
            lblTimer.setText("Rolling back in " + countdownSeconds + " seconds...");
            if (countdownSeconds <= 0) {
                revertResolution(overlay, combo, previousRes);
            }
        });

        confirmationTimeline.getKeyFrames().add(keyFrame);
        confirmationTimeline.play();
    }

    private void revertResolution(StackPane overlay, ComboBox<String> comboResolution, String previousRes) {
        if (confirmationTimeline != null) {
            confirmationTimeline.stop();
        }
        mainView.getRoot().getChildren().remove(overlay);

        applyResolution(previousRes);

        isUpdatingCombo = true;
        comboResolution.setValue(previousRes);
        currentResolution = previousRes;
        isUpdatingCombo = false;
    }
}