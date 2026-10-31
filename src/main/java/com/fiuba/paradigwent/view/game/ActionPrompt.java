package com.fiuba.paradigwent.view.game;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Banner at the top of the board that tells the human what to choose.
 */
public class ActionPrompt extends Label {
    private static final double WIDTH = 420;

    /**
     * @param boardWidth virtual width of the board, used to center the banner
     */
    public ActionPrompt(double boardWidth) {
        setVisible(false);
        setManaged(false);
        setTextFill(Color.WHITE);
        setStyle(
            "-fx-background-color: rgba(12, 15, 22, 0.9);"
                + "-fx-border-color: #ffd45a;"
                + "-fx-border-width: 2;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-padding: 8 18;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;");
        setLayoutX((boardWidth - WIDTH) / 2);
        setLayoutY(18);
        setPrefWidth(WIDTH);
        setAlignment(Pos.CENTER);
    }

    /**
     * @param message instruction to show
     */
    public void show(String message) {
        setText(message);
        setVisible(true);
        setManaged(true);
        toFront();
    }

    /**
     * Hides the banner.
     */
    public void hide() {
        setVisible(false);
        setManaged(false);
    }
}
