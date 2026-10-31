package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fiuba.paradigwent.controller.GameController;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Board drawn in perspective: six attack lines, the side zones (decks,
 * discard piles and weather) and the life gems of both players.
 */
public class Board3DView extends Pane {

    private static final Map<String, Double> RATIOS = Map.ofEntries(
        Map.entry("BOARD_WIDTH", 0.90),
        Map.entry("BOARD_HEIGHT", 1.08),
        Map.entry("BOARD_X", 0.05),
        Map.entry("BOARD_Y", 0.04),
        Map.entry("BOARD_ROW_Y", 0.03),
        Map.entry("BOARD_ROW_OFFSET_X", 0.015),
        Map.entry("BOARD_LINE_GAP", 0.01),
        Map.entry("ROW_WIDTH", 0.64),
        Map.entry("ROW_HEIGHT", 0.16),
        Map.entry("ZONE_X", 0.84),
        Map.entry("ZONE_WIDTH", 0.075),
        Map.entry("ZONE_HEIGHT", 0.14),
        Map.entry("OPPONENT_DECK_Y", 0.02),
        Map.entry("OPPONENT_DISCARD_Y", 0.18),
        Map.entry("PLAYER_DISCARD_Y", 0.82),
        Map.entry("PLAYER_DECK_Y", 0.98),
        Map.entry("WEATHER_Y", 0.50),
        Map.entry("GEM_X", 0.015),
        Map.entry("OPPONENT_FIRST_GEM_Y", 0.02),
        Map.entry("PLAYER_FIRST_GEM_Y", 0.84),
        Map.entry("GEM_SPACING_Y", 0.08),
        Map.entry("CAMERA_LAYOUT_Y", 0.2333333333),
        Map.entry("CAMERA_CONTENT_HEIGHT", 1.12),
        Map.entry("CAMERA_CONTENT_WIDTH", 0.94),
        Map.entry("CONTENT_OFFSET_Y", 0.0740740741),
        Map.entry("CAMERA_ZOOM", 1.0)
    );

    private final Group boardScene;
    private final GameController controller;
    private final RowView[] rows = new RowView[6];
    private final List<GemView> opponentGems = new ArrayList<>();
    private final List<GemView> playerGems = new ArrayList<>();
    private BoardZoneView weatherZone;
    private BoardZoneView opponentDiscardZone;
    private BoardZoneView playerDiscardZone;
    private BoardZoneView opponentDeckZone;
    private BoardZoneView playerDeckZone;
    private int displayedOpponentLives = Player.INITIAL_LIVES;
    private int displayedPlayerLives = Player.INITIAL_LIVES;

    /**
     * @param width      virtual width of the board
     * @param height     virtual height of the board
     * @param controller source of the state of the match
     * @param textures   texture cache of the game view
     */
    public Board3DView(double width, double height, GameController controller, GameTextures textures) {
        this.controller = controller;
        setPrefSize(width, height);

        double boardWidth = width * RATIOS.get("BOARD_WIDTH");
        double boardHeight = height * RATIOS.get("BOARD_HEIGHT");
        double boardX = width * RATIOS.get("BOARD_X");
        double boardLineGap = height * RATIOS.get("BOARD_LINE_GAP");
        double boardRowX = width * (RATIOS.get("BOARD_X")
            + RATIOS.get("BOARD_ROW_OFFSET_X"));
        double boardRowY = height * RATIOS.get("BOARD_ROW_Y");
        double opponentDeckY = height * RATIOS.get("OPPONENT_DECK_Y");
        double opponentDiscardY = height * RATIOS.get("OPPONENT_DISCARD_Y");
        double playerDiscardY = height * RATIOS.get("PLAYER_DISCARD_Y");
        double playerDeckY = height * RATIOS.get("PLAYER_DECK_Y");
        double boardY = height * RATIOS.get("BOARD_Y");
        double zoneX = width * RATIOS.get("ZONE_X");
        double zoneWidth = width * RATIOS.get("ZONE_WIDTH");
        double zoneHeight = height * RATIOS.get("ZONE_HEIGHT");
        double weatherY = height * RATIOS.get("WEATHER_Y") - zoneHeight / 2 + boardY;
        double cameraContentWidth = zoneX + zoneWidth;
        double cameraContentHeight = playerDeckY + zoneHeight;
        double boardLayoutY = height * RATIOS.get("CAMERA_LAYOUT_Y");
        double contentOffsetY = height * RATIOS.get("CONTENT_OFFSET_Y");
        double cameraZoom =
            (height - boardLayoutY - contentOffsetY) / cameraContentHeight
                * RATIOS.get("CAMERA_ZOOM");

        // Central frame of the board
        Rectangle boardFrame = new Rectangle(
            boardWidth,
            boardHeight,
            textures.pattern(GameTextures.BOARD_WOOD, GameColor.BOARD.color()));
        boardFrame.setStroke(GameColor.BOARD_BORDER.color());
        boardFrame.setStrokeWidth(width * 0.002);
        boardFrame.setArcWidth(width * 0.012);
        boardFrame.setArcHeight(width * 0.012);

        // Container of the six attack lines
        VBox boardRows = new VBox(boardLineGap);
        boardRows.setLayoutX(boardRowX);
        boardRows.setLayoutY(boardRowY);
        double rowWidth = width * RATIOS.get("ROW_WIDTH");
        double rowHeight = height * RATIOS.get("ROW_HEIGHT");

        rows[0] = new RowView("Siege", rowWidth, rowHeight,
            controller.getOpponentLine(AttackLineType.SIEGE), textures);
        rows[1] = new RowView("Ranged", rowWidth, rowHeight,
            controller.getOpponentLine(AttackLineType.RANGED), textures);
        rows[2] = new RowView("Melee", rowWidth, rowHeight,
            controller.getOpponentLine(AttackLineType.MELEE), textures);
        rows[3] = new RowView("Melee", rowWidth, rowHeight,
            controller.getPlayerLine(AttackLineType.MELEE), textures);
        rows[4] = new RowView("Ranged", rowWidth, rowHeight,
            controller.getPlayerLine(AttackLineType.RANGED), textures);
        rows[5] = new RowView("Siege", rowWidth, rowHeight,
            controller.getPlayerLine(AttackLineType.SIEGE), textures);
        boardRows.getChildren().addAll(rows);

        Group boardSurface = new Group(boardFrame, boardRows);
        boardSurface.setLayoutX(boardX);
        boardSurface.setLayoutY(boardY);

        Group sideZones = new Group(
            opponentDeckZone = new BoardZoneView("DECK", zoneX, opponentDeckY, zoneWidth, zoneHeight, GameColor.DECK.color()),
            opponentDiscardZone = new BoardZoneView("DISCARD", zoneX, opponentDiscardY, zoneWidth, zoneHeight, GameColor.DISCARD.color()),
            weatherZone = new BoardZoneView("Weather", zoneX, weatherY, zoneWidth, zoneHeight, GameColor.WEATHER.color()),
            playerDiscardZone = new BoardZoneView("DISCARD", zoneX, playerDiscardY, zoneWidth, zoneHeight, GameColor.DISCARD.color()),
            playerDeckZone = new BoardZoneView("DECK", zoneX, playerDeckY, zoneWidth, zoneHeight, GameColor.DECK.color())
        );

        // Life gems, one per life
        Group gemsGroup = new Group();
        double gemSize = width * (24.0 / 1000.0);
        double gemX = width * RATIOS.get("GEM_X");
        double gemSpacing = height * RATIOS.get("GEM_SPACING_Y");
        for (int life = 0; life < Player.INITIAL_LIVES; life++) {
            GemView opponentGem = new GemView(gemX, height * RATIOS.get("OPPONENT_FIRST_GEM_Y") + life * gemSpacing, gemSize);
            GemView playerGem = new GemView(gemX, height * RATIOS.get("PLAYER_FIRST_GEM_Y") + life * gemSpacing, gemSize);
            opponentGems.add(opponentGem);
            playerGems.add(playerGem);
            gemsGroup.getChildren().addAll(opponentGem, playerGem);
        }

        boardScene = new Group(boardSurface, sideZones);

        // Camera
        applyBoardEffect();

        Rectangle cameraBounds = new Rectangle(
            cameraContentWidth,
            cameraContentHeight,
            Color.TRANSPARENT
        );
        Group gemsCameraLayer = new Group(cameraBounds, gemsGroup);
        gemsCameraLayer.setManaged(false);
        gemsCameraLayer.setMouseTransparent(true);
        applyCamera(gemsCameraLayer, cameraContentWidth, cameraContentHeight, cameraZoom, boardLayoutY);
        getChildren().addAll(boardScene, gemsCameraLayer);
        refresh();
    }

    /**
     * Redraws the board from the current state of the match.
     */
    public void refresh() {
        for (RowView row : rows) {
            row.setWeather(controller.getCurrentWeather());
            row.refresh();
        }
        weatherZone.showCard(controller.getCurrentWeatherCard());
        opponentDeckZone.setDeckFaction(controller.getOpponentFaction());
        playerDeckZone.setDeckFaction(controller.getPlayerFaction());
        refreshLives();
        opponentDiscardZone.showCard(controller.getOpponentLastDiscarded());
        playerDiscardZone.showCard(controller.getPlayerLastDiscarded());
        opponentDeckZone.showDeckCount(controller.getOpponentDeckSize());
        playerDeckZone.showDeckCount(controller.getPlayerDeckSize());
    }

    /**
     * @param handler receiver of the hover events of every card on the board
     */
    public void setCardHoverHandler(CardHoverHandler handler) {
        for (RowView row : rows) {
            row.setCardHoverHandler(handler);
        }
        weatherZone.setCardHoverHandler(handler);
        opponentDiscardZone.setCardHoverHandler(handler);
        playerDiscardZone.setCardHoverHandler(handler);
    }

    /**
     * Highlights the minions that can be chosen.
     *
     * @param targets minions that can be chosen
     * @param handler receives the chosen minion
     */
    public void setTargetSelection(List<Minion> targets, Consumer<Minion> handler) {
        Set<Minion> selectable = new HashSet<>(targets);
        for (RowView row : rows) {
            row.setTargetSelection(selectable, handler);
        }
    }

    /**
     * Stops highlighting minions.
     */
    public void clearTargetSelection() {
        for (RowView row : rows) {
            row.setTargetSelection(Set.of(), ignored -> { });
        }
    }

    /**
     * Lets the human click one of its own lines.
     *
     * @param handler receives the chosen line
     */
    public void setLineSelection(Consumer<AttackLineType> handler) {
        for (int index = 3; index < rows.length; index++) {
            rows[index].setLineSelection(handler);
        }
    }

    /**
     * Stops the line selection.
     */
    public void clearLineSelection() {
        for (RowView row : rows) {
            row.clearLineSelection();
        }
    }

    private void refreshLives() {
        int opponentLives = Math.max(0, controller.getOpponentLives());
        int playerLives = Math.max(0, controller.getPlayerLives());
        destroyMissingGems(opponentGems, opponentLives, displayedOpponentLives);
        destroyMissingGems(playerGems, playerLives, displayedPlayerLives);
        displayedOpponentLives = opponentLives;
        displayedPlayerLives = playerLives;
    }

    /**
     * Breaks the gems of the lives lost since the last refresh.
     */
    private void destroyMissingGems(List<GemView> gems, int currentLives, int previousLives) {
        for (int life = currentLives; life < previousLives && life < gems.size(); life++) {
            gems.get(life).destroyGem();
        }
    }

    private void applyBoardEffect() {
        double width = getPrefWidth();
        double height = getPrefHeight();
        double cameraContentWidth = width * RATIOS.get("CAMERA_CONTENT_WIDTH");
        double cameraContentHeight = height * RATIOS.get("CAMERA_CONTENT_HEIGHT");
        double boardLayoutY = height * RATIOS.get("CAMERA_LAYOUT_Y");
        double contentOffsetY = height * RATIOS.get("CONTENT_OFFSET_Y");
        double cameraZoom = ((height - boardLayoutY - contentOffsetY) / cameraContentHeight
            * RATIOS.get("CAMERA_ZOOM")) * 0.92;
        applyCamera(boardScene, cameraContentWidth, cameraContentHeight, cameraZoom, boardLayoutY);
    }

    private void applyCamera(Node node, double width, double height, double zoom, double layoutY) {
        double topInset = width * 0.12;
        double bottomInset = width * 0.02;

        PerspectiveTransform perspective = new PerspectiveTransform();
        perspective.setUlx(topInset);
        perspective.setUly(0);
        perspective.setUrx(width - topInset);
        perspective.setUry(0);
        perspective.setLlx(bottomInset);
        perspective.setLly(height);
        perspective.setLrx(width - bottomInset);
        perspective.setLry(height);
        node.setEffect(perspective);

        node.getTransforms().add(new Scale(zoom, zoom, 0, 0));
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setLayoutY(layoutY);
    }

    /**
     * Moves the scaled content so the board is centered in the window.
     *
     * @param scaledContent group that contains the board
     */
    public void centerInViewport(Group scaledContent) {
        Scene scene = getScene();
        if (scene == null) return;

        Bounds localBounds = boardScene.getBoundsInLocal();
        Point2D topLeft = boardScene.localToScene(localBounds.getMinX(), localBounds.getMinY());
        Point2D topRight = boardScene.localToScene(localBounds.getMaxX(), localBounds.getMinY());
        Point2D bottomLeft = boardScene.localToScene(localBounds.getMinX(), localBounds.getMaxY());
        Point2D bottomRight = boardScene.localToScene(localBounds.getMaxX(), localBounds.getMaxY());
        Point2D boardCenter = average(topLeft, topRight, bottomLeft, bottomRight);

        scaledContent.setTranslateX(
            scaledContent.getTranslateX() + scene.getWidth() / 2 - boardCenter.getX());
        scaledContent.setTranslateY(
            scaledContent.getTranslateY() + scene.getHeight() / 2 - boardCenter.getY());
    }

    private Point2D average(Point2D... points) {
        double x = 0;
        double y = 0;
        for (Point2D point : points) {
            x += point.getX();
            y += point.getY();
        }
        return new Point2D(x / points.length, y / points.length);
    }
}
