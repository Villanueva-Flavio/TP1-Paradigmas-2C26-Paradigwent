package com.fiuba.paradigwent.view.game;

import com.fiuba.paradigwent.controller.GameController;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Panel on the left of the board with the round score of each player,
 * whose turn it is and short notices about the flow of the match.
 */
public class MatchStatusPanel extends VBox {
    private static final double WIDTH = 190;
    private static final String PANEL_STYLE =
        "-fx-background-color: rgba(12, 15, 22, 0.92);"
            + "-fx-border-width: 2;"
            + "-fx-background-radius: 8;"
            + "-fx-border-radius: 8;"
            + "-fx-padding: 8 12;"
            + "-fx-font-weight: bold;";

    private final Label opponentScore = createLabel("#ff8a65", 18);
    private final Label turnIndicator = createLabel("#78a6ff", 16);
    private final Label playerScore = createLabel("#78a6ff", 18);
    private final Label notice = createLabel("#ffd45a", 14);
    private final PauseTransition noticeTimer = new PauseTransition(Duration.seconds(2.5));

    /**
     * @param x horizontal position
     * @param y vertical position
     */
    public MatchStatusPanel(double x, double y) {
        super(10);
        setLayoutX(x);
        setLayoutY(y);
        setAlignment(Pos.CENTER);
        notice.setVisible(false);
        notice.setWrapText(true);
        noticeTimer.setOnFinished(event -> notice.setVisible(false));
        getChildren().addAll(opponentScore, turnIndicator, playerScore, notice);
    }

    /**
     * Shows the round score of each player. Call it together with the board
     * refresh so the numbers always match the cards on the table.
     *
     * @param controller source of the state of the match
     */
    public void updateScores(GameController controller) {
        opponentScore.setText("Opponent: " + controller.getOpponentPower());
        playerScore.setText("You: " + controller.getPlayerPower());
    }

    /**
     * Shows whose turn it is.
     *
     * @param controller source of the state of the match
     */
    public void updateTurn(GameController controller) {
        boolean playerTurn = controller.isPlayerTurn();
        turnIndicator.setText("Turn: " + controller.getCurrentPlayerName());
        turnIndicator.setStyle(PANEL_STYLE + "-fx-font-size: 16px;"
            + "-fx-border-color: " + (playerTurn ? "#78a6ff" : "#ff8a65") + ";");
    }

    /**
     * Shows a short notice for a few seconds.
     *
     * @param message text of the notice
     */
    public void showNotice(String message) {
        notice.setText(message);
        notice.setVisible(true);
        noticeTimer.playFromStart();
    }

    private static Label createLabel(String borderColor, int fontSize) {
        Label label = new Label();
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(WIDTH);
        label.setStyle(PANEL_STYLE + "-fx-font-size: " + fontSize + "px;"
            + "-fx-border-color: " + borderColor + ";");
        return label;
    }
}
