package com.fiuba.paradigwent.view.game.modules;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.fiuba.paradigwent.model.card.Card;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class HandView extends Pane {
    private static final int CARD_COUNT = 10;
    private static final double CARD_SPACING = 52;
    private static final double HAND_SIDE_MARGIN = 36;
    private static final double HAND_BOTTOM_MARGIN = -0;
    private static final double HAND_SCALE = 0.80;

    private final List<Card> cards;
    private final List<CardView> cardViews = new ArrayList<>();
    private final PauseTransition previewDelay = new PauseTransition(Duration.millis(300));
    private Consumer<Card> onPreviewRequested = ignored -> { };
    private Runnable onPreviewCleared = () -> { };
    private Consumer<Card> onCardSelected = ignored -> { };

    /**      * Constructs a new HandView.      */
    public HandView(List<Card> cards, double virtualWidth, double virtualHeight) {
        this.cards = new ArrayList<>(cards);
        setPrefSize(virtualWidth, virtualHeight);
        setScaleX(HAND_SCALE);
        setScaleY(HAND_SCALE);
        setPickOnBounds(false);
        buildCards(virtualWidth, virtualHeight);
    }
    /**
     * Updates the value managed by setOnPreviewRequested.
     * @param callback the value used by the method
     */
    public void setOnPreviewRequested(Consumer<Card> callback) {
        onPreviewRequested = callback;
    }
    /**
     * Updates the value managed by setOnPreviewCleared.
     * @param callback the value used by the method
     */
    public void setOnPreviewCleared(Runnable callback) {
        onPreviewCleared = callback;
    }
    /**
     * Updates the value managed by setOnCardSelected.
     * @param callback the value used by the method
     */
    public void setOnCardSelected(Consumer<Card> callback) {
        onCardSelected = callback == null ? ignored -> { } : callback;
    }
    /**
     * Updates the collection through removeCard.
     * @param card the value used by the method
     */
    public void removeCard(Card card) {
        if (cards.remove(card)) {
            getChildren().clear();
            buildCards(getPrefWidth(), getPrefHeight());
        }
    }
    /**
     * Executes the operation represented by refresh.
     * @param updatedCards the value used by the method
     */
    public void refresh(List<Card> updatedCards) {
        cards.clear();
        cards.addAll(updatedCards);
        getChildren().clear();
        buildCards(getPrefWidth(), getPrefHeight());
    }
    /**
     * Returns the value provided by getCardCenterInScene.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public Point2D getCardCenterInScene(Card card) {
        for (CardView cardView : cardViews) {
            if (cardView.shows(card)) {
                javafx.geometry.Bounds bounds = cardView.localToScene(cardView.getBoundsInLocal());
                return new Point2D(bounds.getCenterX(), bounds.getCenterY());
            }
        }
        return null;
    }
    /**
     * Executes the operation represented by construirCartas.
     * @param virtualWidth the value used by the method
     * @param virtualHeight the value used by the method
     */
    private void buildCards(double virtualWidth, double virtualHeight) {
        cardViews.clear();
        if (cards.isEmpty()) {
            return;
        }

        int visibleCardCount = Math.min(CARD_COUNT, cards.size());
        double center = (visibleCardCount - 1) / 2.0;
        double availableWidth = virtualWidth - HAND_SIDE_MARGIN * 2;
        double spacing = visibleCardCount > 1 ? Math.min(CARD_SPACING, (availableWidth - CardView.HIT_AREA_WIDTH) / (visibleCardCount - 1)) : 0;
        double baseY = virtualHeight - CardView.HIT_AREA_HEIGHT - HAND_BOTTOM_MARGIN;

        for (int index = 0; index < visibleCardCount; index++) {
            double offset = index - center;
            CardView card = new CardView(cards.get(index));
            card.setOnCardClicked(onCardSelected);
            double baseAngle = offset * 3.2;

            Rotate rotation = new Rotate(baseAngle, CardView.HIT_AREA_WIDTH / 2,
                CardView.HIT_AREA_HEIGHT, 0, Rotate.Z_AXIS);
            card.getTransforms().add(rotation);

            card.setLayoutX(virtualWidth / 2 + offset * spacing - CardView.HIT_AREA_WIDTH / 2);
            card.setLayoutY(baseY + Math.abs(offset) * 2);

            configureHover(card, rotation, baseAngle);
            cardViews.add(card);
            getChildren().add(card);
        }
    }
    /**
     * Executes the operation represented by configureHover.
     * @param card the value used by the method
     * @param rotation the value used by the method
     * @param baseAngle the value used by the method
     */
    private void configureHover(CardView card, Rotate rotation, double baseAngle) {
        card.setPickOnBounds(true);
        card.setOnMouseEntered(event -> {
            previewDelay.stop();
            card.toFront();
            card.setScaleX(1.1);
            card.setScaleY(1.1);
            card.setTranslateY(-18);
            rotation.setAngle(0);
            previewDelay.setOnFinished(done -> onPreviewRequested.accept(card.getCard()));
            previewDelay.playFromStart();
        });
        card.setOnMouseMoved(event -> {
            double normalized = (event.getX() - card.getPrefWidth() / 2) / (card.getPrefWidth() / 2);
            rotation.setAngle(-normalized * 8);
        });
        card.setOnMouseExited(event -> {
            previewDelay.stop();
            onPreviewCleared.run();
            Timeline restore = new Timeline(
                new KeyFrame(Duration.millis(200),
                    new KeyValue(card.scaleXProperty(), 1),
                    new KeyValue(card.scaleYProperty(), 1),
                    new KeyValue(card.translateYProperty(), 0),
                    new KeyValue(rotation.angleProperty(), baseAngle)));
            restore.play();
        });
    }
}
