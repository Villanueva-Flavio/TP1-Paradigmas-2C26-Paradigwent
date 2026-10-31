package com.fiuba.paradigwent.view.game;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.view.game.modules.CardVisual;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Animates a played card flying from a hand to the center of the screen.
 */
public class CardFlightAnimation {
    private static final double CARD_HEIGHT = 170;
    private static final Duration FLIGHT = Duration.millis(650);

    private final StackPane container;

    /**
     * @param container node where the flying card is drawn
     */
    public CardFlightAnimation(StackPane container) {
        this.container = container;
    }

    /**
     * Plays the animation.
     *
     * @param card        played card
     * @param opponent    whether the card comes from the opponent's hand
     * @param sourcePoint point of the scene where the card starts, may be {@code null}
     * @param onFinished  action run when the animation ends
     */
    public void play(Card card, boolean opponent, Point2D sourcePoint, Runnable onFinished) {
        CardVisual animatedCard = new CardVisual(card, CardVisual.widthForHeight(CARD_HEIGHT),
            CARD_HEIGHT, "collection-card-image");
        configure(animatedCard, card);
        container.getChildren().add(animatedCard);
        animatedCard.toFront();
        double cardWidth = animatedCard.getPrefWidth();
        double cardHeight = animatedCard.getPrefHeight();
        Point2D localSource = resolveSource(sourcePoint, opponent);
        double startX = localSource.getX() - cardWidth / 2;
        double startY = localSource.getY() - cardHeight / 2;
        double endX = container.getWidth() / 2 - cardWidth / 2;
        double endY = container.getHeight() / 2 - cardHeight / 2;
        animatedCard.relocate(startX, startY);
        ParallelTransition launch = createLaunch(animatedCard, endX - startX, endY - startY, opponent);
        SequentialTransition animation = new SequentialTransition(
            new PauseTransition(Duration.millis(100)), launch, new PauseTransition(Duration.millis(250)));
        animation.setOnFinished(event -> {
            container.getChildren().remove(animatedCard);
            onFinished.run();
        });
        animation.play();
    }

    private void configure(CardVisual visual, Card card) {
        visual.addMetadata(card);
        visual.setMouseTransparent(true);
        visual.setManaged(false);
        visual.setScaleX(0.72);
        visual.setScaleY(0.72);
        visual.setOpacity(0.95);
    }

    private Point2D resolveSource(Point2D sourcePoint, boolean opponent) {
        if (sourcePoint != null) return container.sceneToLocal(sourcePoint);
        return new Point2D(container.getWidth() / 2, opponent ? 80 : container.getHeight() - 80);
    }

    private ParallelTransition createLaunch(CardVisual visual, double moveX, double moveY, boolean opponent) {
        TranslateTransition move = new TranslateTransition(FLIGHT, visual);
        move.setToX(moveX);
        move.setToY(moveY);
        move.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scale = new ScaleTransition(FLIGHT, visual);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition rotate = new RotateTransition(FLIGHT, visual);
        rotate.setFromAngle(opponent ? -12 : 12);
        rotate.setToAngle(opponent ? -4 : 4);
        rotate.setInterpolator(Interpolator.EASE_OUT);

        return new ParallelTransition(move, scale, rotate);
    }
}
