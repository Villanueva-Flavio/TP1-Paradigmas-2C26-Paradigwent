package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class CardView extends StackPane {
    public static final double WIDTH = 86;
    public static final double HEIGHT = 126;
    public static final double HIT_AREA_WIDTH = 100;
    public static final double HIT_AREA_HEIGHT = 150;

    private final CardRace race;
    private final ImageView imageView;

    public CardView(CardRace race) {
        this.race = race;
        imageView = new ImageView(new Image(
            getClass().getResource("/images/cards/" + race.getAssetName()).toExternalForm()));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        imageView.getStyleClass().add("hand-card");

        setPrefSize(HIT_AREA_WIDTH, HIT_AREA_HEIGHT);
        setMinSize(HIT_AREA_WIDTH, HIT_AREA_HEIGHT);
        getChildren().add(imageView);
    }

    public CardRace getRace() {
        return race;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
