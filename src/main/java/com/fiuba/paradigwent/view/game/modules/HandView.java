package com.fiuba.paradigwent.view.game.modules;

import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class HandView extends Pane {
    private static final int CARD_COUNT = 10;
    private static final double CARD_SPACING = 52;

    private final CardRace race;
    private final PauseTransition previewDelay = new PauseTransition(Duration.millis(500));
    private Consumer<CardRace> onPreviewRequested = ignored -> { };
    private Runnable onPreviewCleared = () -> { };

    public HandView(CardRace race, double virtualWidth, double virtualHeight) {
        this.race = race;
        setPrefSize(virtualWidth, virtualHeight);
        setPickOnBounds(false);
        construirCartas(virtualWidth, virtualHeight);
    }

    public void setOnPreviewRequested(Consumer<CardRace> callback) {
        onPreviewRequested = callback;
    }

    public void setOnPreviewCleared(Runnable callback) {
        onPreviewCleared = callback;
    }

    private void construirCartas(double virtualWidth, double virtualHeight) {
        double center = (CARD_COUNT - 1) / 2.0;
        double baseY = virtualHeight - 150; 

        for (int index = 0; index < CARD_COUNT; index++) {
            double offset = index - center;
            CardView card = new CardView(race);
            double baseAngle = offset * 3.2;

            Rotate rotation = new Rotate(baseAngle, CardView.WIDTH / 2, CardView.HEIGHT, 0, Rotate.Z_AXIS);
            card.getImageView().getTransforms().add(rotation);

            card.setLayoutX(virtualWidth / 2 + offset * CARD_SPACING - CardView.HIT_AREA_WIDTH / 2);
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
            card.getImageView().setScaleX(1.1);
            card.getImageView().setScaleY(1.1);
            card.getImageView().setTranslateY(-18);
            rotation.setAngle(0);
            previewDelay.setOnFinished(done -> onPreviewRequested.accept(race));
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
                    new KeyValue(card.getImageView().scaleXProperty(), 1),
                    new KeyValue(card.getImageView().scaleYProperty(), 1),
                    new KeyValue(card.getImageView().translateYProperty(), 0),
                    new KeyValue(rotation.angleProperty(), baseAngle)));
            restore.play();
        });
    }
}