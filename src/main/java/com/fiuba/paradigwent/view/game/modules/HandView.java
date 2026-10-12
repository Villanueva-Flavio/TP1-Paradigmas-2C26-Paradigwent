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
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class HandView extends Pane {
    private static final int CARD_COUNT = 10;
    private static final double CARD_SPACING = 52;
    private static final double HAND_SIDE_MARGIN = 36;
    private static final double HAND_BOTTOM_MARGIN = 22;

    private final List<Card> cards;
    private final PauseTransition previewDelay = new PauseTransition(Duration.millis(500));
    private Consumer<Card> onPreviewRequested = ignored -> { };
    private Runnable onPreviewCleared = () -> { };
    private Consumer<Card> onCardSelected = ignored -> { };

    public HandView(List<Card> cards, double virtualWidth, double virtualHeight) {
        this.cards = new ArrayList<>(cards);
        setPrefSize(virtualWidth, virtualHeight);
        setPickOnBounds(false);
        addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleCardPressed);
        construirCartas(virtualWidth, virtualHeight);
    }

    public void setOnPreviewRequested(Consumer<Card> callback) {
        onPreviewRequested = callback;
    }

    public void setOnPreviewCleared(Runnable callback) {
        onPreviewCleared = callback;
    }

    public void setOnCardSelected(Consumer<Card> callback) {
        onCardSelected = callback == null ? ignored -> { } : callback;
    }

    public void removeCard(Card card) {
        if (cards.remove(card)) {
            getChildren().clear();
            construirCartas(getPrefWidth(), getPrefHeight());
        }
    }

    public void refresh(List<Card> updatedCards) {
        cards.clear();
        cards.addAll(updatedCards);
        getChildren().clear();
        construirCartas(getPrefWidth(), getPrefHeight());
    }

    private void handleCardPressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        CardView selectedCardView = findCardView(event.getTarget());
        if (selectedCardView == null) {
            return;
        }

        onCardSelected.accept(selectedCardView.getCard());
        event.consume();
    }

    private CardView findCardView(Object target) {
        Node node = (Node) target;
        while (node != null && node != this) {
            if (node.getClass() == CardView.class) {
                return (CardView) node;
            }
            node = node.getParent();
        }
        return null;
    }

    private void construirCartas(double virtualWidth, double virtualHeight) {
        if (cards.isEmpty()) {
            return;
        }

        int visibleCardCount = Math.min(CARD_COUNT, cards.size());
        double center = (visibleCardCount - 1) / 2.0;
        double availableWidth = virtualWidth - HAND_SIDE_MARGIN * 2;
        double spacing = visibleCardCount > 1
            ? Math.min(CARD_SPACING,
                (availableWidth - CardView.HIT_AREA_WIDTH) / (visibleCardCount - 1))
            : 0;
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
            getChildren().add(card);
        }
    }

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