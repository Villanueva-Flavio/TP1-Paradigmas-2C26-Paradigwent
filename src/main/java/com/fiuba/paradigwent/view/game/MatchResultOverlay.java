package com.fiuba.paradigwent.view.game;

import com.fiuba.paradigwent.model.enums.Result;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Dimmed overlay with the result of the match and a button back to the menu.
 */
public class MatchResultOverlay extends StackPane {

    /**
     * @param result     result of the match (the human is player 1)
     * @param detail     explanation shown under the title
     * @param width      width of the covered area
     * @param height     height of the covered area
     * @param backToMenu action of the "back to menu" button
     */
    public MatchResultOverlay(Result result, String detail, double width, double height, Runnable backToMenu) {
        setPrefSize(width, height);
        setPickOnBounds(true);
        setStyle("-fx-background-color: rgba(5, 8, 16, 0.78);");
        getChildren().add(createPanel(result, detail, backToMenu));
    }

    private StackPane createPanel(Result result, String detail, Runnable backToMenu) {
        StackPane panel = new StackPane();
        panel.setPrefSize(420, 260);
        panel.setMaxSize(420, 260);
        panel.setStyle("-fx-background-color: rgba(25, 32, 50, 0.98);"
                    +  "-fx-border-color: #d8aa55; -fx-border-width: 3;"
                    +  "-fx-background-radius: 14; -fx-border-radius: 14;");

        panel.getChildren().addAll(createTitle(result), createMessage(detail), createMenuButton(backToMenu));
        StackPane.setAlignment(panel, Pos.CENTER);
        return panel;
    }

    private Label createTitle(Result result) {
        Label title = new Label(titleFor(result));
        title.setStyle("-fx-text-fill: #f4d58d; -fx-font-size: 34px; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(48, 0, 0, 0));
        return title;
    }

    private Label createMessage(String detail) {
        Label message = new Label(detail);
        message.setStyle("-fx-text-fill: #e7eaf2; -fx-font-size: 16px;");
        StackPane.setAlignment(message, Pos.CENTER);
        return message;
    }

    private Button createMenuButton(Runnable backToMenu) {
        Button button = new Button("BACK TO MENU");
        button.setOnAction(event -> backToMenu.run());
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setMargin(button, new Insets(0, 0, 20, 0));
        return button;
    }

    private String titleFor(Result result) {
        return switch (result) {
            case PLAYER1_WIN -> "VICTORY!";
            case PLAYER2_WIN -> "DEFEAT";
            case DRAW -> "DRAW";
        };
    }
}
