package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.view.game.GameView;
import com.fiuba.paradigwent.model.card.Card;

import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.function.BiConsumer;

public class BoardZoneView extends StackPane {
    private final Text label;
    private final double width;
    private final double height;
    private BiConsumer<Card, BoardZoneView> cardHoverHandler = (card, source) -> { };

    public BoardZoneView(String labelText, double x, double y, double width, double height, Color strokeColor) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        setMaxSize(width, height);
        this.width = width;
        this.height = height;

        Rectangle fondo = new Rectangle(width, height, GameView.COLOR.get("BoardBackground"));
        fondo.setStroke(strokeColor);
        fondo.setStrokeWidth(width * (1.5 / 60.0));
        fondo.setArcWidth(width * 0.1);
        fondo.setArcHeight(width * 0.1);

        label = new Text(labelText);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, width * (11.0 / 60.0)));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFill(Color.LIGHTGRAY);

        getChildren().addAll(fondo, label);
    }

    public void showCard(Card card) {
        clearContent();
        label.setVisible(card == null);
        if (card == null) {
            return;
        }
        CardVisual visual = new CardVisual(card, width * 0.9, height * 0.9, "board-card");
        visual.addMetadata(card);
        getChildren().add(visual);
        visual.setOnMouseEntered(event -> cardHoverHandler.accept(card, this));
        visual.setOnMouseExited(event -> cardHoverHandler.accept(null, this));
        visual.setScaleX(0.2);
        visual.setScaleY(0.2);
        visual.setOpacity(0);
        javafx.animation.ScaleTransition scale =
            new javafx.animation.ScaleTransition(javafx.util.Duration.millis(280), visual);
        scale.setToX(1);
        scale.setToY(1);
        javafx.animation.FadeTransition fade =
            new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), visual);
        fade.setToValue(1);
        scale.play();
        fade.play();
    }

    public void setCardHoverHandler(BiConsumer<Card, BoardZoneView> handler) {
        cardHoverHandler = handler == null ? (card, source) -> { } : handler;
    }

    public void showDeckCount(int count) {
        clearContent();
        label.setVisible(true);
        label.setText("DECK\n" + count);
        if (count <= 0) {
            return;
        }

        int visibleCards = Math.min(5, count);
        double cardWidth = width * 0.78;
        double cardHeight = height * 0.82;
        for (int index = visibleCards - 1; index >= 0; index--) {
            ImageView back = new ImageView(new Image(resource(
                backAssetPath()), true));
            back.setPreserveRatio(true);
            back.setFitWidth(cardWidth);
            back.setFitHeight(cardHeight);
            back.setTranslateX(index * width * 0.025);
            back.setTranslateY(index * height * 0.018);
            back.setMouseTransparent(true);
            getChildren().add(1, back);
        }
    }

    public void setDeckFaction(String faction) {
        setUserData(faction);
    }

    private void clearContent() {
        getChildren().removeIf(node -> node != label
            && node != getChildren().get(0));
    }

    private static String resource(String path) {
        return BoardZoneView.class.getResource(path).toExternalForm();
    }

    private String backAssetPath() {
        String faction = getUserData() == null ? "murgol" : getUserData().toString();
        String path = "/images/cards/card-back-" + faction.toLowerCase() + ".png";
        return BoardZoneView.class.getResource(path) == null
            ? "/images/cards/card-back-murgol.png" : path;
    }
}