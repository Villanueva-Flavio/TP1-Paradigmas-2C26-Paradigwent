package com.fiuba.paradigwent.view.game.modules;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.List;

import com.fiuba.paradigwent.view.game.GameView;

import java.util.ArrayList;

public class GemView extends Group {
    private List<Rectangle> gemParts = new ArrayList<>();
    private final double size;

    private void setGemSpecifications(Rectangle gemPart, double rotation, double translateX, double translateY, double size) {
        gemPart.setRotate(rotation);
        gemPart.setTranslateX(translateX * size / 24.0);
        gemPart.setTranslateY(translateY * size / 24.0);
        gemPart.setArcHeight(size * 5.0 / 24.0);
        gemPart.setArcWidth(size * 5.0 / 24.0);
    }

    private void setGemAnimation(Group gem, double scaleX, double scaleY, double duration) {
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(gem.scaleXProperty(), 1.0),
                new KeyValue(gem.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.seconds(duration),
                new KeyValue(gem.scaleXProperty(), scaleX),
                new KeyValue(gem.scaleYProperty(), scaleY)
            )
        );
        
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }

    public GemView(double x, double y, double size) {
        this.size = size;
        setLayoutX(x);
        setLayoutY(y);

        gemParts.add(new Rectangle(size, size, GameView.COLOR.get("Gem")));
        gemParts.add(new Rectangle(size, size, GameView.COLOR.get("Gem")));
        gemParts.add(new Rectangle(size, size, GameView.COLOR.get("Gem")));

        setGemSpecifications(gemParts.get(0), 45, 0, 8, size);
        setGemSpecifications(gemParts.get(1), 48, -10, -2, size);
        setGemSpecifications(gemParts.get(2), 43, 10, -2, size);

        Group heartContainer = new Group(gemParts.get(0), gemParts.get(1), gemParts.get(2));
        heartContainer.setEffect(new Glow(0.5));

        setGemAnimation(heartContainer, 1.2, 1.2, 0.8);        

        getChildren().add(heartContainer);
    }

    public void destroyGem(){
        Timeline fadeOut = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(this.opacityProperty(), 1.0)
            ),
            new KeyFrame(Duration.seconds(0.5),
                new KeyValue(this.opacityProperty(), 0.0),
                new KeyValue(gemParts.get(0).translateXProperty(), gemParts.get(0).getTranslateX() - size * 10 / 24),
                new KeyValue(gemParts.get(0).translateYProperty(), gemParts.get(0).getTranslateY() - size * 10 / 24),
                new KeyValue(gemParts.get(1).translateXProperty(), gemParts.get(1).getTranslateX() + size * 10 / 24),
                new KeyValue(gemParts.get(1).translateYProperty(), gemParts.get(1).getTranslateY() - size * 10 / 24),
                new KeyValue(gemParts.get(2).translateXProperty(), gemParts.get(2).getTranslateX() - size * 10 / 24),
                new KeyValue(gemParts.get(2).translateYProperty(), gemParts.get(2).getTranslateY() + size * 10 / 24)
            )
        );

        fadeOut.setOnFinished(event -> {
            this.setVisible(false);
        });

        fadeOut.play();
    }
}