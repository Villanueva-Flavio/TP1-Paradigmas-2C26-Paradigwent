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

import com.fiuba.paradigwent.view.game.GameView;

import java.util.ArrayList;

public class Board3DView extends Pane {

    private static final double BOARD_WIDTH_RATIO = 0.90;
    private static final double BOARD_HEIGHT_RATIO = 1.3333333333;
    private static final double BOARD_X_RATIO = 0.08;
    private static final double BOARD_Y_RATIO = 0.0333333333;
    private static final double BOARD_ROW_Y_RATIO = 0.03;
    private static final double BOARD_LINE_GAP_RATIO = 0.01;
    private static final double ROW_WIDTH_RATIO = 0.70;
    private static final double ROW_HEIGHT_RATIO = 0.20;
    private static final double ZONE_X_RATIO = 0.89;
    private static final double ZONE_WIDTH_RATIO = 0.06;
    private static final double ZONE_HEIGHT_RATIO = 0.1666666667;
    private static final double OPPONENT_DECK_Y_RATIO = 0.0633333333;
    private static final double OPPONENT_DISCARD_Y_RATIO = 0.2466666667;
    private static final double PLAYER_DISCARD_Y_RATIO = 0.9791666667;
    private static final double PLAYER_DECK_Y_RATIO = 1.1625;
    private static final double WEATHER_Y_RATIO = 0.5;
    private static final double GEM_X_RATIO = 0.035;
    private static final double OPPONENT_GEM_Y_RATIO = 0.05;
    private static final double OPPONENT_SECOND_GEM_Y_RATIO = 0.1666666667;
    private static final double PLAYER_GEM_Y_RATIO = 0.95;
    private static final double PLAYER_SECOND_GEM_Y_RATIO = 1.05;
    private static final double CAMERA_LAYOUT_Y_RATIO = 0.2333333333;
    private static final double CAMERA_CONTENT_HEIGHT_RATIO = 1.3291666667;
    private static final double CAMERA_CONTENT_WIDTH_RATIO = 0.95;
    private static final double CONTENT_OFFSET_Y_RATIO = 0.0740740741;
    public static final double CAMERA_ZOOM_MULTIPLIER = 1.0;

    private final Group tablero2_5d;

    public Board3DView(double width, double height) {
        this(width, height, CardRace.HUMAN);
    }

    public Board3DView(double width, double height, CardRace race) {
        setPrefSize(width, height);

        double boardWidth = width * BOARD_WIDTH_RATIO;
        double boardHeight = height * BOARD_HEIGHT_RATIO;
        double boardX = width * BOARD_X_RATIO;
        double boardLineGap = height * BOARD_LINE_GAP_RATIO;
        double boardRowX = width * BOARD_X_RATIO;
        double boardRowY = height * BOARD_ROW_Y_RATIO;
        double opponentDeckY = height * OPPONENT_DECK_Y_RATIO;
        double opponentDiscardY = height * OPPONENT_DISCARD_Y_RATIO;
        double playerDiscardY = height * PLAYER_DISCARD_Y_RATIO;
        double playerDeckY = height * PLAYER_DECK_Y_RATIO;
        double boardY = height * BOARD_Y_RATIO;
        double zoneX = width * ZONE_X_RATIO;
        double zoneWidth = width * ZONE_WIDTH_RATIO;
        double zoneHeight = height * ZONE_HEIGHT_RATIO;
        double weatherY = height * WEATHER_Y_RATIO - zoneHeight / 2 + boardY;
        double gemX = width * GEM_X_RATIO;
        double opponentGemY = height * OPPONENT_GEM_Y_RATIO;
        double opponentSecondGemY = height * OPPONENT_SECOND_GEM_Y_RATIO;
        double playerGemY = height * PLAYER_GEM_Y_RATIO;
        double playerSecondGemY = height * PLAYER_SECOND_GEM_Y_RATIO;
        double cameraContentWidth = zoneX + zoneWidth;
        double cameraContentHeight = playerDeckY + zoneHeight;
        double boardLayoutY = height * CAMERA_LAYOUT_Y_RATIO;
        double contentOffsetY = height * CONTENT_OFFSET_Y_RATIO;
        double cameraZoom =
            (height - boardLayoutY - contentOffsetY) / cameraContentHeight
                * CAMERA_ZOOM_MULTIPLIER;

        // Marco central del tablero
        Rectangle marcoTablero = new Rectangle(boardWidth, boardHeight, GameView.COLOR.get("Board"));
        marcoTablero.setStroke(GameView.COLOR.get("BoardBorder"));
        marcoTablero.setStrokeWidth(width * 0.002);
        marcoTablero.setArcWidth(width * 0.012);
        marcoTablero.setArcHeight(width * 0.012);

        // Contenedor de las 6 filas
        VBox filasTablero = new VBox(boardLineGap);
        filasTablero.setLayoutX(boardRowX);
        filasTablero.setLayoutY(boardRowY);
        double rowWidth = width * ROW_WIDTH_RATIO;
        double rowHeight = height * ROW_HEIGHT_RATIO;

        filasTablero.getChildren().addAll(
            new RowView("Siege", GameView.COLOR.get("SiegeLine"), rowWidth, rowHeight),
            new RowView("Ranged", GameView.COLOR.get("RangedLine"), rowWidth, rowHeight),
            new RowView("Melee", GameView.COLOR.get("MeleeLine"), rowWidth, rowHeight),
            new RowView("Melee", GameView.COLOR.get("MeleeLine"), rowWidth, rowHeight),
            new RowView("Ranged", GameView.COLOR.get("RangedLine"), rowWidth, rowHeight),
            new RowView("Siege", GameView.COLOR.get("SiegeLine"), rowWidth, rowHeight)
        );

        Group superficieTablero = new Group(marcoTablero, filasTablero);
        superficieTablero.setLayoutX(boardX);
        superficieTablero.setLayoutY(boardY);

        Group zonasLaterales = new Group(
            new DeckView(race, zoneX, opponentDeckY, zoneWidth, zoneHeight),
            new BoardZoneView("DISCARD", zoneX, opponentDiscardY, zoneWidth, zoneHeight, GameView.COLOR.get("Discard")),
            new BoardZoneView("Weather", zoneX, weatherY, zoneWidth, zoneHeight, GameView.COLOR.get("Weather")),
            new BoardZoneView("DISCARD", zoneX, playerDiscardY, zoneWidth, zoneHeight, GameView.COLOR.get("Discard")),
            new DeckView(race, zoneX, playerDeckY, zoneWidth, zoneHeight)
        );


        // Gemas de vida en el tablero
        List<GemView> gems = new ArrayList<GemView>();
        double gemSize = width * (24.0 / 1000.0);
        gems.add(new GemView(gemX, opponentSecondGemY, gemSize));
        gems.add(new GemView(gemX, playerGemY, gemSize));
        gems.add(new GemView(gemX, opponentGemY, gemSize));
        gems.add(new GemView(gemX, playerSecondGemY, gemSize));
        
        
        new Thread(() -> {         // TESTING de destroyGem cada uno
            try {
                Thread.sleep(5000);
                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    javafx.application.Platform.runLater(() -> {
                        gems.get(index).destroyGem();
                    });
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Group gemasGroup = new Group(gems.get(0), gems.get(1), gems.get(2), gems.get(3));

        

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
    }

    private void aplicarEfectoMesa() {
        double width = getPrefWidth();
        double height = getPrefHeight();
        double cameraContentWidth = width * CAMERA_CONTENT_WIDTH_RATIO;
        double cameraContentHeight = height * CAMERA_CONTENT_HEIGHT_RATIO;
        double boardLayoutY = height * CAMERA_LAYOUT_Y_RATIO;
        double contentOffsetY = height * CONTENT_OFFSET_Y_RATIO;
        double cameraZoom = ((height - boardLayoutY - contentOffsetY) / cameraContentHeight * CAMERA_ZOOM_MULTIPLIER) * 1.1;
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