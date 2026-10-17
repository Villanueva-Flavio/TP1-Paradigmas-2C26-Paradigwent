package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.view.game.GameView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
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
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;

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
    private final Rectangle background;
    private final Rectangle weatherOverlay;
    private final Pane weatherParticles;
    private final FadeTransition weatherAnimation;
    private final List<TranslateTransition> particleAnimations = new ArrayList<>();
    private boolean lineSelectionActive;
    private WeatherType displayedWeather = WeatherType.CLEAR;
    private BiConsumer<Minion, StackPane> cardHoverHandler = (card, source) -> { };
    private Set<Minion> selectableCards = Collections.emptySet();
    private Consumer<Minion> cardSelectionHandler = ignored -> { };

    /**      * Constructs a new RowView.      */
    public RowView(String labelText, Color strokeColor, double rowWidth, double rowHeight, AttackLine line) {
        this.rowWidth = rowWidth;
        this.rowHeight = rowHeight;
        this.line = line;
        this.lineSelectionActive = false;
        setPrefSize(rowWidth, rowHeight);
        setMaxSize(rowWidth, rowHeight);

        background = new Rectangle(
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

        weatherOverlay = new Rectangle(rowWidth, rowHeight);
        weatherOverlay.setMouseTransparent(true);
        weatherOverlay.setManaged(false);
        weatherOverlay.setVisible(false);
        weatherAnimation = new FadeTransition(Duration.seconds(2.8), weatherOverlay);
        weatherAnimation.setFromValue(0.35);
        weatherAnimation.setToValue(0.75);
        weatherAnimation.setAutoReverse(true);
        weatherAnimation.setCycleCount(FadeTransition.INDEFINITE);

        weatherParticles = new Pane();
        weatherParticles.setPrefSize(rowWidth, rowHeight);
        weatherParticles.setMinSize(rowWidth, rowHeight);
        weatherParticles.setMaxSize(rowWidth, rowHeight);
        weatherParticles.setMouseTransparent(true);
        weatherParticles.setManaged(false);
        weatherParticles.setVisible(false);
        weatherParticles.setClip(new Rectangle(rowWidth, rowHeight));

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
        getChildren().addAll(background, weatherOverlay, weatherParticles, text, slots);
        refresh();
    }
    /**
     * Executes the operation represented by refresh.
     */
    public void refresh() {
        slots.getChildren().clear();
        updateWeatherOverlay();
        updateLineHighlight(false);
        List<Minion> centeredMinions = centeredMinions();
        for (Minion minion : centeredMinions) {
            StackPane slot = minion == null ? emptySlot() : cardSlot(minion);
            slots.getChildren().add(slot);
        }
    }
    /**
     * Updates the value managed by setWeather.
     * @param weather the value used by the method
     */
    public void setWeather(WeatherType weather) {
        WeatherType nextWeather = weather == null ? WeatherType.CLEAR : weather;
        if (displayedWeather == nextWeather) {
            return;
        }
        stopParticles();
        displayedWeather = nextWeather;
        updateWeatherOverlay();
    }
    /**
     * Executes the operation represented by updateWeatherOverlay.
     */
    private void updateWeatherOverlay() {
        boolean affected = line.isWeathered() && displayedWeather != WeatherType.CLEAR;
        weatherOverlay.setVisible(affected);
        if (!affected) {
            weatherAnimation.stop();
            weatherOverlay.setOpacity(0);
            stopParticles();
            return;
        }

        weatherOverlay.setFill(weatherGradient(displayedWeather));
        weatherOverlay.setOpacity(0.35);
        weatherParticles.setVisible(true);
        if (particleAnimations.isEmpty()) {
            createParticles(displayedWeather);
        }
        if (weatherAnimation.getStatus() != javafx.animation.Animation.Status.RUNNING) {
            weatherAnimation.play();
        }
    }
    /**
     * Executes the operation represented by createParticles.
     * @param weather the value used by the method
     */
    private void createParticles(WeatherType weather) {
        stopParticles();
        int particleCount = weather == WeatherType.FOG ? 8 : 14;
        for (int index = 0; index < particleCount; index++) {
            double x = 18 + ((index * 67) % Math.max(1, (int) rowWidth - 36));
            double y = 10 + ((index * 29) % Math.max(1, (int) rowHeight - 20));
            Circle particle = new Circle(particleRadius(weather, index), particleColor(weather, index));
            particle.setCenterX(x);
            particle.setCenterY(y);
            weatherParticles.getChildren().add(particle);

            TranslateTransition movement = new TranslateTransition(
                Duration.seconds(particleDuration(weather, index)), particle);
            movement.setFromY(weather == WeatherType.RAIN ? -rowHeight * 0.35 : rowHeight * 0.20);
            movement.setToY(weather == WeatherType.RAIN ? rowHeight * 0.35 : -rowHeight * 0.20);
            movement.setFromX(weather == WeatherType.RAIN ? 0 : -rowWidth * 0.08);
            movement.setToX(weather == WeatherType.RAIN ? rowWidth * 0.04 : rowWidth * 0.08);
            movement.setAutoReverse(true);
            movement.setCycleCount(TranslateTransition.INDEFINITE);
            movement.play();
            particleAnimations.add(movement);
        }
    }
    /**
     * Executes the operation represented by particleRadius.
     * @param weather the value used by the method
     * @param index the value used by the method
     * @return the value produced by the method
     */
    private double particleRadius(WeatherType weather, int index) {
        if (weather == WeatherType.RAIN) {
            return 1.2 + (index % 3) * 0.5;
        }
        return 2.0 + (index % 3) * 0.8;
    }
    /**
     * Executes the operation represented by particleColor.
     * @param weather the value used by the method
     * @param index the value used by the method
     * @return the value produced by the method
     */
    private Color particleColor(WeatherType weather, int index) {
        return switch (weather) {
            case FOG -> Color.rgb(235, 240, 245, 0.18);
            case RAIN -> Color.rgb(145, 210, 255, 0.55);
            case THUNDER -> Color.rgb(245, 232, 150, 0.65);
            case FROST, SNOW -> Color.rgb(245, 252, 255, 0.72);
            case CLEAR -> Color.TRANSPARENT;
        };
    }
    /**
     * Executes the operation represented by particleDuration.
     * @param weather the value used by the method
     * @param index the value used by the method
     * @return the value produced by the method
     */
    private double particleDuration(WeatherType weather, int index) {
        double base = weather == WeatherType.RAIN ? 1.15 : 2.4;
        return base + (index % 4) * 0.25;
    }
    /**
     * Executes the operation represented by stopParticles.
     */
    private void stopParticles() {
        particleAnimations.forEach(TranslateTransition::stop);
        particleAnimations.clear();
        weatherParticles.getChildren().clear();
        weatherParticles.setVisible(false);
    }
    /**
     * Executes the operation represented by weatherGradient.
     * @param weather the value used by the method
     * @return the value produced by the method
     */
    private LinearGradient weatherGradient(WeatherType weather) {
        Color primary;
        Color secondary;
        switch (weather) {
            case FROST -> {
                primary = Color.rgb(185, 235, 255, 0.52);
                secondary = Color.rgb(70, 145, 220, 0.16);
            }
            case FOG -> {
                primary = Color.rgb(235, 240, 245, 0.40);
                secondary = Color.rgb(95, 105, 120, 0.18);
            }
            case RAIN -> {
                primary = Color.rgb(55, 150, 235, 0.45);
                secondary = Color.rgb(15, 65, 145, 0.20);
            }
            case THUNDER -> {
                primary = Color.rgb(150, 125, 225, 0.42);
                secondary = Color.rgb(245, 205, 65, 0.20);
            }
            case SNOW -> {
                primary = Color.rgb(250, 253, 255, 0.58);
                secondary = Color.rgb(120, 205, 245, 0.18);
            }
            case CLEAR -> {
                primary = Color.TRANSPARENT;
                secondary = Color.TRANSPARENT;
            }
            default -> throw new IllegalStateException("Unsupported weather: " + weather);
        }
        return new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.REFLECT,
            new Stop(0, secondary),
            new Stop(0.42, primary),
            new Stop(0.58, primary),
            new Stop(1, secondary));
    }
    /**
     * Updates the value managed by setCardHoverHandler.
     * @param Minion the value used by the method
     * @param handler the value used by the method
     */
    public void setCardHoverHandler(BiConsumer<Minion, StackPane> handler) {
        cardHoverHandler = handler == null ? (card, source) -> { } : handler;
        refresh();
    }
    /**
     * Updates the value managed by setTargetSelection.
     * @param cards the value used by the method
     * @param handler the value used by the method
     */
    public void setTargetSelection(Set<Minion> cards, Consumer<Minion> handler) {
        selectableCards = cards == null ? Collections.emptySet() : cards;
        cardSelectionHandler = handler == null ? ignored -> { } : handler;
        refresh();
    }
    /**
     * Updates the value managed by setLineSelection.
     * @param handler the value used by the method
     */
    public void setLineSelection(Consumer<AttackLineType> handler) {
        lineSelectionActive = true;
        setOnMouseEntered(event -> updateLineHighlight(true));
        setOnMouseExited(event -> updateLineHighlight(false));
        setOnMouseClicked(event -> handler.accept(line.getType()));
        refresh();
    }
    /**
     * Updates the collection through clearLineSelection.
     */
    public void clearLineSelection() {
        lineSelectionActive = false;
        setOnMouseEntered(null);
        setOnMouseExited(null);
        setOnMouseClicked(null);
        updateLineHighlight(false);
    }
    /**
     * Executes the operation represented by updateLineHighlight.
     * @param hovering the value used by the method
     */
    private void updateLineHighlight(boolean hovering) {
        if (line.hasHorn()) {
            DropShadow glow = new DropShadow(42, Color.web("#ffd45a"));
            glow.setSpread(0.72);
            background.setEffect(glow);
        } else if (lineSelectionActive && hovering) {
            DropShadow glow = new DropShadow(34, Color.web("#fff1a3"));
            glow.setSpread(0.55);
            background.setEffect(glow);
        } else {
            background.setEffect(null);
        }
    }
    /**
     * Executes the operation represented by emptySlot.
     * @return the value produced by the method
     */
    private StackPane emptySlot() {
        double cardHeight = cardHeightForSlot();
        double cardWidth = cardHeight * BOARD_CARD_ASPECT_RATIO;
        StackPane result = new StackPane();
        result.setPrefSize(cardWidth, cardHeight);
        return result;
    }
    /**
     * Executes the operation represented by cardSlot.
     * @param minion the value used by the method
     * @return the value produced by the method
     */
    private StackPane cardSlot(Minion minion) {
        double cardHeight = cardHeightForSlot();
        double cardWidth = cardHeight * BOARD_CARD_ASPECT_RATIO;
        CardVisual visual = new CardVisual(minion, cardWidth, cardHeight, "board-card",
            BOARD_CARD_ASPECT_RATIO);
        int displayedPower = line.getEffectivePower(minion) * (line.hasHorn() ? 2 : 1);
        visual.addMetadata(minion, false, displayedPower);
        StackPane result = new StackPane(visual);
        result.setUserData(displayedPower);
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
    /**
     * Executes the operation represented by cardHeightForSlot.
     * @return the value produced by the method
     */
    private double cardHeightForSlot() {
        double gap = rowWidth * SLOT_GAP_RATIO;
        double availableWidth = rowWidth * (1 - LABEL_SPACE_RATIO);
        double maxCardWidth = (availableWidth - gap * (SLOT_COUNT - 1)) / SLOT_COUNT;
        return Math.min(rowHeight * 0.98, maxCardWidth / BOARD_CARD_ASPECT_RATIO);
    }
    /**
     * Executes the operation represented by centeredMinions.
     * @return the value produced by the method
     */
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
