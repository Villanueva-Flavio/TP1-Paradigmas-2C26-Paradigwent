package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.view.game.GameView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.Set;
import java.util.Collections;

public class RowView extends StackPane {
    private static final int SLOT_COUNT = 9;
    private static final double BOARD_CARD_ASPECT_RATIO = 550.0 / 910.0;
    private static final double BOARD_CARD_SCALE = 1.25;
    private static final double BOARD_CARD_HOVER_SCALE = 1.42;
    private static final double SLOT_GAP_RATIO = 4.0 / 700.0;
    private static final double LABEL_SPACE_RATIO = 58.0 / 700.0;
    private final HBox slots;
    private final double rowWidth;
    private final double rowHeight;
    private final AttackLine line;
    private BiConsumer<Minion, StackPane> cardHoverHandler = (card, source) -> { };
    private Set<Minion> selectableCards = Collections.emptySet();
    private Consumer<Minion> cardSelectionHandler = ignored -> { };

    public RowView(String labelText, Color strokeColor, double rowWidth, double rowHeight, AttackLine line) {
        this.rowWidth = rowWidth;
        this.rowHeight = rowHeight;
        this.line = line;
        setPrefSize(rowWidth, rowHeight);
        setMaxSize(rowWidth, rowHeight);

        Rectangle background = new Rectangle(
            rowWidth,
            rowHeight,
            GameTextures.pattern(GameTextures.SLOT_WOOD, GameView.COLOR.get("Row")));
        InnerShadow carvedEdge = new InnerShadow();
        carvedEdge.setRadius(rowHeight * 0.16);
        carvedEdge.setChoke(0.32);
        carvedEdge.setOffsetX(0);
        carvedEdge.setOffsetY(2);
        carvedEdge.setColor(Color.rgb(35, 18, 8, 0.78));

        DropShadow raisedEdge = new DropShadow();
        raisedEdge.setRadius(rowHeight * 0.08);
        raisedEdge.setOffsetY(3);
        raisedEdge.setSpread(0.16);
        raisedEdge.setColor(Color.rgb(20, 10, 4, 0.72));
        raisedEdge.setInput(carvedEdge);
        background.setEffect(raisedEdge);
        background.setStroke(Color.rgb(74, 40, 20, 0.9));
        background.setStrokeWidth(rowWidth * (0.8 / 700.0));
        background.setArcWidth(rowWidth * (8.0 / 700.0));
        background.setArcHeight(rowWidth * (8.0 / 700.0));

        Text text = new Text(labelText);
        text.setFont(Font.font("System", FontWeight.BOLD, rowHeight * 0.1));
        text.setFill(GameView.COLOR.get("RowText"));
        StackPane.setAlignment(text, Pos.CENTER_LEFT);
        text.setTranslateX(rowWidth * (15.0 / 700.0));

        slots = new HBox(rowWidth * SLOT_GAP_RATIO);
        slots.setAlignment(Pos.CENTER_LEFT);
        slots.setLayoutX(rowWidth * LABEL_SPACE_RATIO);
        slots.setPrefWidth(rowWidth * (1 - LABEL_SPACE_RATIO));
        slots.setMaxWidth(rowWidth * (1 - LABEL_SPACE_RATIO));
        getChildren().addAll(background, text, slots);
        refresh();
    }

    public void refresh() {
        slots.getChildren().clear();
        List<Minion> centeredMinions = centeredMinions();
        for (Minion minion : centeredMinions) {
            StackPane slot = minion == null ? emptySlot() : cardSlot(minion);
            slots.getChildren().add(slot);
        }
    }

    public void setCardHoverHandler(BiConsumer<Minion, StackPane> handler) {
        cardHoverHandler = handler == null ? (card, source) -> { } : handler;
        refresh();
    }

    public void setTargetSelection(Set<Minion> cards, Consumer<Minion> handler) {
        selectableCards = cards == null ? Collections.emptySet() : cards;
        cardSelectionHandler = handler == null ? ignored -> { } : handler;
        refresh();
    }

    private StackPane emptySlot() {
        double cardHeight = cardHeightForSlot();
        double cardWidth = cardHeight * BOARD_CARD_ASPECT_RATIO;
        StackPane result = new StackPane();
        result.setPrefSize(cardWidth, cardHeight);
        return result;
    }

    private StackPane cardSlot(Minion minion) {
        double cardHeight = cardHeightForSlot();
        double cardWidth = cardHeight * BOARD_CARD_ASPECT_RATIO;
        CardVisual visual = new CardVisual(minion, cardWidth, cardHeight, "board-card",
            BOARD_CARD_ASPECT_RATIO);
        visual.addMetadata(minion);
        StackPane result = new StackPane(visual);
        result.setPrefSize(visual.getPrefWidth(), visual.getPrefHeight());
        result.setMinSize(visual.getPrefWidth(), visual.getPrefHeight());
        result.setPickOnBounds(true);
        visual.setScaleX(BOARD_CARD_SCALE);
        visual.setScaleY(BOARD_CARD_SCALE);
        visual.setOpacity(0);
        result.setOpacity(1);

        boolean selectable = selectableCards.contains(minion);
        if (selectable) {
            visual.setEffect(new DropShadow(14, Color.web("#ffd45a")));
            result.setOnMouseClicked(event -> cardSelectionHandler.accept(minion));
            FadeTransition glow = new FadeTransition(Duration.millis(720), visual);
            glow.setFromValue(0.78);
            glow.setToValue(1.0);
            glow.setAutoReverse(true);
            glow.setCycleCount(FadeTransition.INDEFINITE);
            glow.play();
        }

        result.setOnMouseEntered(event -> {
            visual.setScaleX(BOARD_CARD_HOVER_SCALE);
            visual.setScaleY(BOARD_CARD_HOVER_SCALE);
            visual.setEffect(selectable
                ? new DropShadow(28, Color.web("#fff1a3"))
                : new DropShadow(18, Color.BLACK));
            cardHoverHandler.accept(minion, result);
        });

        result.setOnMouseExited(event -> {
            visual.setScaleX(BOARD_CARD_SCALE);
            visual.setScaleY(BOARD_CARD_SCALE);
            visual.setEffect(selectable
                ? new DropShadow(14, Color.web("#ffd45a")) : null);
            cardHoverHandler.accept(null, result);
        });

        ScaleTransition scale = new ScaleTransition(Duration.millis(260), visual);
        scale.setToX(BOARD_CARD_SCALE);
        scale.setToY(BOARD_CARD_SCALE);
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

    private double cardHeightForSlot() {
        double gap = rowWidth * SLOT_GAP_RATIO;
        double availableWidth = rowWidth * (1 - LABEL_SPACE_RATIO);
        double maxCardWidth = (availableWidth - gap * (SLOT_COUNT - 1)) / SLOT_COUNT;
        return Math.min(rowHeight * 0.98, maxCardWidth / BOARD_CARD_ASPECT_RATIO);
    }

    private List<Minion> centeredMinions() {
        List<Minion> result = new ArrayList<>(SLOT_COUNT);
        for (int index = 0; index < SLOT_COUNT; index++) {
            result.add(null);
        }

        List<Minion> minions = line.getMinions();
        int center = 4;
        for (int index = 0; index < minions.size() && index < 9; index++) {
            int distance = (index + 1) / 2;
            int direction = index % 2 == 0 ? 1 : -1;
            int slotIndex = index == 0 ? center : center + direction * distance;
            result.set(slotIndex, minions.get(index));
        }
        return result;
    }
}
