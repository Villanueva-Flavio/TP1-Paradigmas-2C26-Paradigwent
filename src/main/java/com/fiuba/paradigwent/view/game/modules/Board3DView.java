package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Map;

import com.fiuba.paradigwent.view.game.GameView;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.Set;
import java.util.HashSet;

public class Board3DView extends Pane {

    private static final Map<String, Double> RATIOS = Map.ofEntries(
        Map.entry("BOARD_WIDTH", 0.90),
        Map.entry("BOARD_HEIGHT", 1.08),
        Map.entry("BOARD_X", 0.05),
        Map.entry("BOARD_Y", 0.04),
        Map.entry("BOARD_ROW_Y", 0.03),
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
        Map.entry("OPPONENT_GEM_Y", 0.10),
        Map.entry("OPPONENT_SECOND_GEM_Y", 0.18),
        Map.entry("PLAYER_GEM_Y", 0.84),
        Map.entry("PLAYER_SECOND_GEM_Y", 0.92),
        Map.entry("CAMERA_LAYOUT_Y", 0.2333333333),
        Map.entry("CAMERA_CONTENT_HEIGHT", 1.12),
        Map.entry("CAMERA_CONTENT_WIDTH", 0.94),
        Map.entry("CONTENT_OFFSET_Y", 0.0740740741),
        Map.entry("CAMERA_ZOOM", 1.0)
    );

    private final Group tablero2_5d;
    private final Board board;
    private final RowView[] rows = new RowView[6];
    private BoardZoneView weatherZone;
    private BoardZoneView opponentDiscardZone;
    private BoardZoneView playerDiscardZone;
    private BoardZoneView opponentDeckZone;
    private BoardZoneView playerDeckZone;
    private GemView opponentFirstGem;
    private GemView opponentSecondGem;
    private GemView playerFirstGem;
    private GemView playerSecondGem;
    private int displayedOpponentLives = 2;
    private int displayedPlayerLives = 2;

    public Board3DView(double width, double height) {
        this(width, height, null);
    }

    public Board3DView(double width, double height, Board board) {
        this.board = board;
        setPrefSize(width, height);

        double boardWidth = width * RATIOS.get("BOARD_WIDTH");
        double boardHeight = height * RATIOS.get("BOARD_HEIGHT");
        double boardX = width * RATIOS.get("BOARD_X");
        double boardLineGap = height * RATIOS.get("BOARD_LINE_GAP");
        double boardRowX = width * RATIOS.get("BOARD_X");
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
        double gemX = width * RATIOS.get("GEM_X");
        double opponentGemY = height * RATIOS.get("OPPONENT_GEM_Y");
        double opponentSecondGemY = height * RATIOS.get("OPPONENT_SECOND_GEM_Y");
        double playerGemY = height * RATIOS.get("PLAYER_GEM_Y");
        double playerSecondGemY = height * RATIOS.get("PLAYER_SECOND_GEM_Y");
        double cameraContentWidth = zoneX + zoneWidth;
        double cameraContentHeight = playerDeckY + zoneHeight;
        double boardLayoutY = height * RATIOS.get("CAMERA_LAYOUT_Y");
        double contentOffsetY = height * RATIOS.get("CONTENT_OFFSET_Y");
        double cameraZoom =
            (height - boardLayoutY - contentOffsetY) / cameraContentHeight
                * RATIOS.get("CAMERA_ZOOM");

        // Marco central del tablero
        Rectangle marcoTablero = new Rectangle(
            boardWidth,
            boardHeight,
            GameTextures.pattern(GameTextures.BOARD_WOOD, GameView.COLOR.get("Board")));
        marcoTablero.setStroke(GameView.COLOR.get("BoardBorder"));
        marcoTablero.setStrokeWidth(width * 0.002);
        marcoTablero.setArcWidth(width * 0.012);
        marcoTablero.setArcHeight(width * 0.012);

        // Contenedor de las 6 filas
        VBox filasTablero = new VBox(boardLineGap);
        filasTablero.setLayoutX(boardRowX);
        filasTablero.setLayoutY(boardRowY);
        double rowWidth = width * RATIOS.get("ROW_WIDTH");
        double rowHeight = height * RATIOS.get("ROW_HEIGHT");

        PlayerSide opponent = board == null ? null : board.getOpponentSide();
        PlayerSide player = board == null ? null : board.getPlayerSide();
        rows[0] = new RowView("Siege", GameView.COLOR.get("SiegeLine"), rowWidth, rowHeight,
            line(opponent, AttackLineType.SIEGE));
        rows[1] = new RowView("Ranged", GameView.COLOR.get("RangedLine"), rowWidth, rowHeight,
            line(opponent, AttackLineType.RANGED));
        rows[2] = new RowView("Melee", GameView.COLOR.get("MeleeLine"), rowWidth, rowHeight,
            line(opponent, AttackLineType.MELEE));
        rows[3] = new RowView("Melee", GameView.COLOR.get("MeleeLine"), rowWidth, rowHeight,
            line(player, AttackLineType.MELEE));
        rows[4] = new RowView("Ranged", GameView.COLOR.get("RangedLine"), rowWidth, rowHeight,
            line(player, AttackLineType.RANGED));
        rows[5] = new RowView("Siege", GameView.COLOR.get("SiegeLine"), rowWidth, rowHeight,
            line(player, AttackLineType.SIEGE));
        filasTablero.getChildren().addAll(rows);

        Group superficieTablero = new Group(marcoTablero, filasTablero);
        superficieTablero.setLayoutX(boardX);
        superficieTablero.setLayoutY(boardY);

        Group zonasLaterales = new Group(
            opponentDeckZone = new BoardZoneView("DECK", zoneX, opponentDeckY, zoneWidth, zoneHeight, GameView.COLOR.get("Deck")),
            opponentDiscardZone = new BoardZoneView("DISCARD", zoneX, opponentDiscardY, zoneWidth, zoneHeight, GameView.COLOR.get("Discard")),
            weatherZone = new BoardZoneView("Weather", zoneX, weatherY, zoneWidth, zoneHeight, GameView.COLOR.get("Weather")),
            playerDiscardZone = new BoardZoneView("DISCARD", zoneX, playerDiscardY, zoneWidth, zoneHeight, GameView.COLOR.get("Discard")),
            playerDeckZone = new BoardZoneView("DECK", zoneX, playerDeckY, zoneWidth, zoneHeight, GameView.COLOR.get("Deck"))
        );


        // Gemas de vida en el tablero
        double gemSize = width * (24.0 / 1000.0);
        opponentSecondGem = new GemView(gemX, opponentSecondGemY, gemSize);
        playerFirstGem = new GemView(gemX, playerGemY, gemSize);
        opponentFirstGem = new GemView(gemX, opponentGemY, gemSize);
        playerSecondGem = new GemView(gemX, playerSecondGemY, gemSize);
        
        Group gemasGroup = new Group(
            opponentSecondGem, playerFirstGem, opponentFirstGem, playerSecondGem);

        

        tablero2_5d = new Group(superficieTablero, zonasLaterales);

        // Camara

        aplicarEfectoMesa();

        Rectangle limitesCamara = new Rectangle(
            cameraContentWidth,
            cameraContentHeight,
            javafx.scene.paint.Color.TRANSPARENT
        );
        Group gemasCameraLayer = new Group(limitesCamara, gemasGroup);
        gemasCameraLayer.setManaged(false);
        gemasCameraLayer.setMouseTransparent(true);
        aplicarCamara(gemasCameraLayer, cameraContentWidth, cameraContentHeight, cameraZoom, boardLayoutY);
        getChildren().addAll(tablero2_5d, gemasCameraLayer);
        refresh();
    }

    private com.fiuba.paradigwent.model.board.AttackLine line(PlayerSide side, AttackLineType type) {
        return side == null ? new com.fiuba.paradigwent.model.board.AttackLine(type)
            : side.getAttackLine(type);
    }

    public void refresh() {
        for (RowView row : rows) {
            if (row != null) {
                row.refresh();
            }
        }
        if (weatherZone != null && board != null) {
            weatherZone.showCard(board.getCurrentWeatherCard());
        }
        if (board != null) {
            opponentDeckZone.setDeckFaction(
                board.getOpponentSide().getOwner().getFaction());
            playerDeckZone.setDeckFaction(
                board.getPlayerSide().getOwner().getFaction());
            refreshLives();
            showLatestDiscard(opponentDiscardZone, board.getOpponentSide().getOwner().getDiscard());
            showLatestDiscard(playerDiscardZone, board.getPlayerSide().getOwner().getDiscard());
            opponentDeckZone.showDeckCount(board.getOpponentSide().getOwner().getDeckSize());
            playerDeckZone.showDeckCount(board.getPlayerSide().getOwner().getDeckSize());
        }
    }

    public void setCardHoverHandler(BiConsumer<Card, javafx.scene.Node> handler) {
        BiConsumer<Card, javafx.scene.Node> safeHandler =
            handler == null ? (card, source) -> { } : handler;
        for (RowView row : rows) {
            row.setCardHoverHandler((card, source) -> safeHandler.accept(card, source));
        }
        BiConsumer<Card, BoardZoneView> zoneHandler =
            (card, source) -> safeHandler.accept(card, source);
        weatherZone.setCardHoverHandler(zoneHandler);
        opponentDiscardZone.setCardHoverHandler(zoneHandler);
        playerDiscardZone.setCardHoverHandler(zoneHandler);
    }

    public void setTargetSelection(List<Card> targets, Consumer<Card> handler) {
        Set<com.fiuba.paradigwent.model.card.Minion> selectable = new HashSet<>();
        if (targets != null) {
            for (Card target : targets) {
                if (target instanceof com.fiuba.paradigwent.model.card.Minion minion) {
                    selectable.add(minion);
                }
            }
        }
        Consumer<com.fiuba.paradigwent.model.card.Minion> selectionHandler =
            minion -> handler.accept(minion);
        for (RowView row : rows) {
            row.setTargetSelection(selectable, selectionHandler);
        }
    }

    public void clearTargetSelection() {
        for (RowView row : rows) {
            row.setTargetSelection(Set.of(), ignored -> { });
        }
    }

    private void refreshLives() {
        int opponentLives = Math.max(0, Math.min(2,
            board.getOpponentSide().getOwner().getLives()));
        int playerLives = Math.max(0, Math.min(2,
            board.getPlayerSide().getOwner().getLives()));

        destroyMissingGems(opponentLives, displayedOpponentLives,
            opponentFirstGem, opponentSecondGem);
        destroyMissingGems(playerLives, displayedPlayerLives,
            playerFirstGem, playerSecondGem);

        displayedOpponentLives = opponentLives;
        displayedPlayerLives = playerLives;
    }

    private void destroyMissingGems(int currentLives, int previousLives,
            GemView firstGem, GemView secondGem) {
        if (currentLives < previousLives) {
            if (currentLives < 1 && previousLives >= 1) {
                firstGem.destroyGem();
            }
            if (currentLives < 2 && previousLives >= 2) {
                secondGem.destroyGem();
            }
        }
    }

    private void showLatestDiscard(BoardZoneView zone, List<Card> discard) {
        if (zone != null) {
            zone.showCard(discard.isEmpty() ? null : discard.get(discard.size() - 1));
        }
    }

    private void aplicarEfectoMesa() {
        double width = getPrefWidth();
        double height = getPrefHeight();
        double cameraContentWidth = width * RATIOS.get("CAMERA_CONTENT_WIDTH");
        double cameraContentHeight = height * RATIOS.get("CAMERA_CONTENT_HEIGHT");
        double boardLayoutY = height * RATIOS.get("CAMERA_LAYOUT_Y");
        double contentOffsetY = height * RATIOS.get("CONTENT_OFFSET_Y");
        double cameraZoom = ((height - boardLayoutY - contentOffsetY) / cameraContentHeight
            * RATIOS.get("CAMERA_ZOOM")) * 0.92;
        aplicarCamara(tablero2_5d, cameraContentWidth, cameraContentHeight, cameraZoom, boardLayoutY);
    }

    private void aplicarCamara(Node node, double width, double height, double zoom, double layoutY) {
        double topInset = width * 0.12;
        double bottomInset = width * 0.02;

        PerspectiveTransform perspectiva = new PerspectiveTransform();
        perspectiva.setUlx(topInset);
        perspectiva.setUly(0);
        perspectiva.setUrx(width - topInset);
        perspectiva.setUry(0);
        perspectiva.setLlx(bottomInset);
        perspectiva.setLly(height);
        perspectiva.setLrx(width - bottomInset);
        perspectiva.setLry(height);
        node.setEffect(perspectiva);

        Scale cameraScale = new Scale(
            zoom,
            zoom,
            0,
            0
        );
        node.getTransforms().add(cameraScale);
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setLayoutY(layoutY);
    }

    public void centerInViewport(Group scaledContent) {
        javafx.scene.Scene scene = getScene();
        if (scene == null) return;

        Bounds localBounds = tablero2_5d.getBoundsInLocal();
        Point2D topLeft = tablero2_5d.localToScene(localBounds.getMinX(), localBounds.getMinY());
        Point2D topRight = tablero2_5d.localToScene(localBounds.getMaxX(), localBounds.getMinY());
        Point2D bottomLeft = tablero2_5d.localToScene(localBounds.getMinX(), localBounds.getMaxY());
        Point2D bottomRight = tablero2_5d.localToScene(localBounds.getMaxX(), localBounds.getMaxY());
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