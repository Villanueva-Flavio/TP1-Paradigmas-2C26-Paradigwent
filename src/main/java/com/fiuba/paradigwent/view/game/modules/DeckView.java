package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;

import com.fiuba.paradigwent.view.game.GameView;

public class DeckView extends StackPane {
    /**      * Constructs a new DeckView.      */
    public DeckView(CardRace race, double x, double y, double width, double height) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        setMaxSize(width, height);

        Rectangle background = new Rectangle(width, height, GameView.COLOR.get("BoardBackground"));
        background.setStroke(GameView.COLOR.get("Deck"));
        background.setStrokeWidth(width * (1.5 / 60.0));
        background.setArcWidth(width * 0.1);
        background.setArcHeight(width * 0.1);

        Rectangle card = new Rectangle(width * 0.6, height * 0.72, GameView.COLOR.get("Deck"));
        card.setStroke(Color.LIGHTGRAY);
        card.setStrokeWidth(1);
        card.setArcWidth(width * 0.08);
        card.setArcHeight(width * 0.08);

        Text label = new Text("DECK " + race.name());
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, width * (8.0 / 60.0)));
        label.setFill(Color.LIGHTGRAY);
        label.setTranslateY(height * 0.38);

        getChildren().addAll(background, card, label);
    }
}
