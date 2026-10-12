package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.view.game.GameView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class RowView extends StackPane {
    private final HBox slots;
    private final double rowWidth;
    private final double rowHeight;
    private final AttackLine line;

    public RowView(String labelText, Color strokeColor, double rowWidth,
                   double rowHeight, AttackLine line) {
        this.rowWidth = rowWidth;
        this.rowHeight = rowHeight;
        this.line = line;
        setPrefSize(rowWidth, rowHeight);
        setMaxSize(rowWidth, rowHeight);

        Rectangle background = new Rectangle(rowWidth, rowHeight, GameView.COLOR.get("Row"));
        background.setStroke(strokeColor);
        background.setStrokeWidth(rowWidth * (1.5 / 700.0));
        background.setArcWidth(rowWidth * (8.0 / 700.0));
        background.setArcHeight(rowWidth * (8.0 / 700.0));

        Text text = new Text(labelText);
        text.setFont(Font.font("System", FontWeight.BOLD, rowHeight * 0.1));
        text.setFill(GameView.COLOR.get("RowText"));
        StackPane.setAlignment(text, Pos.CENTER_LEFT);
        text.setTranslateX(rowWidth * (15.0 / 700.0));

        slots = new HBox(rowWidth * (5.0 / 700.0));
        slots.setAlignment(Pos.CENTER);
        slots.setTranslateX(rowWidth * (20.0 / 700.0));
        getChildren().addAll(background, text, slots);
        refresh();
    }

    public void refresh() {
        slots.getChildren().clear();
        for (int index = 0; index < 9; index++) {
            Minion minion = index < line.getMinions().size()
                ? line.getMinions().get(index) : null;
            StackPane slot = minion == null ? emptySlot() : cardSlot(minion);
            slots.getChildren().add(slot);
        }
    }

    private StackPane emptySlot() {
        Rectangle dummy = new Rectangle(rowWidth * (60.0 / 700.0),
            rowHeight * (100.0 / 120.0), GameView.COLOR.get("DummyCard"));
        dummy.setStroke(GameView.COLOR.get("DummyCardStroke"));
        dummy.setStrokeWidth(rowWidth * (1.0 / 700.0));
        dummy.setArcWidth(rowWidth * (4.0 / 700.0));
        dummy.setArcHeight(rowHeight * (4.0 / 700.0));
        dummy.setEffect(new DropShadow(rowWidth * (4.0 / 700.0),
            GameView.COLOR.get("DummyCardShadow")));
        StackPane result = new StackPane(dummy);
        result.setPrefSize(dummy.getWidth(), dummy.getHeight());
        return result;
    }

    private StackPane cardSlot(Minion minion) {
        double cardWidth = rowWidth * (68.0 / 700.0);
        double cardHeight = rowHeight * (104.0 / 120.0);
        CardVisual visual = new CardVisual(minion, cardWidth, cardHeight, "board-card");
        visual.addMetadata(minion);
        StackPane result = new StackPane(visual);
        result.setPrefSize(visual.getPrefWidth(), visual.getPrefHeight());
        result.setMinSize(visual.getPrefWidth(), visual.getPrefHeight());
        result.setPickOnBounds(true);
        visual.setScaleX(0.85);
        visual.setScaleY(0.85);
        visual.setOpacity(0);
        result.setOpacity(1);
        result.setOnMouseEntered(event -> {
            visual.setScaleX(1.16);
            visual.setScaleY(1.16);
            visual.setEffect(new DropShadow(18, Color.BLACK));
        });
        result.setOnMouseExited(event -> {
            visual.setScaleX(1);
            visual.setScaleY(1);
            visual.setEffect(null);
        });
        ScaleTransition scale = new ScaleTransition(Duration.millis(260), visual);
        scale.setToX(1);
        scale.setToY(1);
        FadeTransition fade = new FadeTransition(Duration.millis(180), visual);
        fade.setToValue(1);
        TranslateTransition drop = new TranslateTransition(Duration.millis(260), visual);
        drop.setFromY(-28);
        drop.setToY(0);
        scale.play();
        fade.play();
        drop.play();
        return result;
    }
}
