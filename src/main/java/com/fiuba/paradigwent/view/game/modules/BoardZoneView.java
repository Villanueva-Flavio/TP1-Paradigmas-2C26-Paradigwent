package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.view.game.GameView;
import com.fiuba.paradigwent.model.card.Card;

import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
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

    /**      * Constructs a new BoardZoneView.      */
    public BoardZoneView(String labelText, double x, double y, double width, double height, Color strokeColor) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        setMaxSize(width, height);
        this.width = width;
        this.height = height;

        Rectangle fondo = new Rectangle(width, height, GameView.COLOR.get("BoardBackground"));
        InnerShadow carvedEdge = new InnerShadow();
        carvedEdge.setRadius(width * 0.12);
        carvedEdge.setChoke(0.3);
        carvedEdge.setOffsetY(2);
        carvedEdge.setColor(Color.rgb(35, 18, 8, 0.75));

        DropShadow raisedEdge = new DropShadow();
        raisedEdge.setRadius(width * 0.08);
        raisedEdge.setOffsetY(2);
        raisedEdge.setSpread(0.12);
        raisedEdge.setColor(Color.rgb(20, 10, 4, 0.7));
        raisedEdge.setInput(carvedEdge);
        fondo.setEffect(raisedEdge);
        fondo.setStroke(Color.rgb(74, 40, 20, 0.9));
        fondo.setStrokeWidth(width * (0.8 / 60.0));
        fondo.setArcWidth(width * 0.1);
        fondo.setArcHeight(width * 0.1);

        label = new Text(labelText);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, width * (11.0 / 60.0)));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFill(Color.LIGHTGRAY);

        getChildren().addAll(fondo, label);
    }
    /**
     * Displays the information handled by showCard.
     * @param card the value used by the method
     */
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
    /**
     * Updates the value managed by setCardHoverHandler.
     * @param Card the value used by the method
     * @param handler the value used by the method
     */
    public void setCardHoverHandler(BiConsumer<Card, BoardZoneView> handler) {
        cardHoverHandler = handler == null ? (card, source) -> { } : handler;
    }
    /**
     * Displays the information handled by showDeckCount.
     * @param count the value used by the method
     */
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
    /**
     * Updates the value managed by setDeckFaction.
     * @param faction the value used by the method
     */
    public void setDeckFaction(String faction) {
        setUserData(faction);
    }
    /**
     * Updates the collection through clearContent.
     */
    private void clearContent() {
        getChildren().removeIf(node -> node != label
            && node != getChildren().get(0));
    }
    /**
     * Executes the operation represented by resource.
     * @param path the value used by the method
     * @return the value produced by the method
     */
    private static String resource(String path) {
        return BoardZoneView.class.getResource(path).toExternalForm();
    }
    /**
     * Executes the operation represented by backAssetPath.
     * @return the value produced by the method
     */
    private String backAssetPath() {
        String faction = getUserData() == null ? "murgol" : getUserData().toString();
        String path = "/images/cards/card-back-" + faction.toLowerCase() + ".png";
        return BoardZoneView.class.getResource(path) == null
            ? "/images/cards/card-back-murgol.png" : path;
    }
}
