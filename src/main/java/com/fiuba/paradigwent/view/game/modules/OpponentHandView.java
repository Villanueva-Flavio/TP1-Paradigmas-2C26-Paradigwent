package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

/**
 * Displays only the backs of the opponent's cards, keeping the hand contents hidden.
 */
public class OpponentHandView extends Pane {
    private static final int MAX_VISIBLE_CARDS = 10;
    private static final double CARD_WIDTH = CardView.HIT_AREA_WIDTH;
    private static final double CARD_HEIGHT = CardView.HIT_AREA_HEIGHT;
    private static final double CARD_STEP = 52;
    private static final double HAND_SCALE = 0.65;

    /**      * Constructs a new OpponentHandView.      */
    public OpponentHandView(double x, double y) {
        setLayoutX(x);
        setLayoutY(y - 100);
        setPrefSize(CARD_WIDTH, CARD_HEIGHT + 24);
        setScaleX(HAND_SCALE);
        setScaleY(HAND_SCALE);
        setRotate(180);
        setMouseTransparent(true);
    }
    /**
     * Displays the information handled by showHand.
     * @param cardCount the value used by the method
     * @param faction the value used by the method
     */
    public void showHand(int cardCount, String faction) {
        getChildren().clear();
        int visibleCards = Math.min(MAX_VISIBLE_CARDS, Math.max(0, cardCount));
        String path = "/images/cards/card-back-" + faction.toLowerCase() + ".png";
        setPrefWidth(visibleCards == 0 ? CARD_WIDTH : CARD_WIDTH + CARD_STEP * (visibleCards - 1));
        for (int index = 0; index < visibleCards; index++) {
            ImageView back = new ImageView(new Image(getClass().getResource(path).toExternalForm(), true));
            back.setFitWidth(CardView.WIDTH);
            back.setFitHeight(CardView.HEIGHT);
            back.setPreserveRatio(true);
            double offset = index - (visibleCards - 1) / 2.0;
            Rotate rotation = new Rotate(offset * -3.2, CardView.HIT_AREA_WIDTH / 2, CardView.HIT_AREA_HEIGHT);
            back.getTransforms().add(rotation);
            back.setLayoutX(getPrefWidth() / 2 + offset * CARD_STEP - CardView.HIT_AREA_WIDTH / 2);
            back.setLayoutY(Math.abs(offset) * 2);
            getChildren().add(back);
        }
    }
    /**
     * Returns the value provided by getDrawPointInScene.
     * @return the value produced by the method
     */
    public Point2D getDrawPointInScene() {
        javafx.geometry.Bounds bounds = localToScene(getBoundsInLocal());
        return new Point2D(bounds.getCenterX(), bounds.getMinY() + CARD_HEIGHT / 2);
    }
}
