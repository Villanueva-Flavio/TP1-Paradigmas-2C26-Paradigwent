package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Faction;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;

/**
 * Side zone of the board that shows a deck, a discard pile or the weather card.
 */
public class BoardZoneView extends StackPane {
    private static final Faction DEFAULT_BACK_FACTION = Faction.MURGOL;

    private final Text label;
    private final double width;
    private final double height;
    private CardHoverHandler cardHoverHandler = new IgnoredCardHover();
    private Faction deckFaction = DEFAULT_BACK_FACTION;

    /**
     * @param labelText   text shown while the zone is empty
     * @param x           horizontal position
     * @param y           vertical position
     * @param width       width of the zone
     * @param height      height of the zone
     * @param strokeColor accent color of the zone
     */
    public BoardZoneView(String labelText, double x, double y, double width, double height, Color strokeColor) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        setMaxSize(width, height);
        this.width = width;
        this.height = height;

        Rectangle background = new Rectangle(width, height, GameColor.BOARD_BACKGROUND.color());
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
        background.setEffect(raisedEdge);
        background.setStroke(Color.rgb(74, 40, 20, 0.9));
        background.setStrokeWidth(width * (0.8 / 60.0));
        background.setArcWidth(width * 0.1);
        background.setArcHeight(width * 0.1);

        label = new Text(labelText);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, width * (11.0 / 60.0)));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFill(Color.LIGHTGRAY);

        getChildren().addAll(background, label);
    }

    /**
     * Shows a card face up, or the empty label when the card is {@code null}.
     *
     * @param card card to show
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
        visual.setOnMouseEntered(event -> cardHoverHandler.cardHovered(card, this, card.getDisplayedPower()));
        visual.setOnMouseExited(event -> cardHoverHandler.hoverEnded());
        visual.setScaleX(0.2);
        visual.setScaleY(0.2);
        visual.setOpacity(0);
        ScaleTransition scale = new ScaleTransition(Duration.millis(280), visual);
        scale.setToX(1);
        scale.setToY(1);
        FadeTransition fade = new FadeTransition(Duration.millis(200), visual);
        fade.setToValue(1);
        scale.play();
        fade.play();
    }

    /**
     * @param handler receiver of the hover events of the shown card
     */
    public void setCardHoverHandler(CardHoverHandler handler) {
        cardHoverHandler = handler == null ? new IgnoredCardHover() : handler;
    }

    /**
     * Shows a pile of card backs with the number of remaining cards.
     *
     * @param count cards left in the deck
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
            ImageView back = new ImageView(new Image(resource(backAssetPath()), true));
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
     * @param faction faction whose card back is shown on the deck
     */
    public void setDeckFaction(Faction faction) {
        deckFaction = faction == null ? DEFAULT_BACK_FACTION : faction;
    }

    private void clearContent() {
        getChildren().removeIf(node -> node != label
            && node != getChildren().get(0));
    }

    private String resource(String path) {
        return BoardZoneView.class.getResource(path).toExternalForm();
    }

    private String backAssetPath() {
        String path = "/images/cards/card-back-" + deckFaction.name().toLowerCase() + ".png";
        return BoardZoneView.class.getResource(path) == null
            ? "/images/cards/card-back-" + DEFAULT_BACK_FACTION.name().toLowerCase() + ".png" : path;
    }
}
