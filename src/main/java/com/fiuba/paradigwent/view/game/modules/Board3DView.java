package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.util.List;

import com.fiuba.paradigwent.view.game.GameView;

import java.util.ArrayList;

public class Board3DView extends Pane {

    private static final double CAMERA_POSITION_X = 0;
    private static final double CAMERA_POSITION_Y = 0;
    private static final double BOARD_WIDTH = 900;
    private static final double BOARD_X = 80;
    private static final double BOARD_LINE_GAP = 6;
    private static final double BOARD_ROW_X = 80;
    private static final double BOARD_ROW_Y = 18;
    private static final double OPPONENT_DECK_Y = 38;
    private static final double OPPONENT_DISCARD_Y = 148;
    private static final double PLAYER_DISCARD_Y = 587.5;
    private static final double PLAYER_DECK_Y = 697.5;
    private static final double WEATHER_Y = GameView.BOARD_HEIGHT / 2 - GameView.ZONE_HEIGHT / 2 + GameView.BOARD_Y;
    private static final double GEM_X = 35;
    private static final double OPPONENT_GEM_Y = 30;
    private static final double OPPONENT_SECOND_GEM_Y = 100;
    private static final double PLAYER_GEM_Y = 570;
    private static final double PLAYER_SECOND_GEM_Y = 630;
    private static final double CAMERA_CONTENT_WIDTH = GameView.ZONE_X + GameView.ZONE_WIDTH;
    private static final double CAMERA_CONTENT_HEIGHT = 700;
    private static final double BORDER_WIDTH = 2;
    private static final double BOARD_CORNER_RADIUS = 12;
    
    public static final double CAMERA_TOP_MARGIN = 0.12;
    public static final double BOARD_LAYOUT_Y = 55;
    public static final double CAMERA_BOTTOM_MARGIN = 0.02;
    
    private final Group tablero2_5d;

    public Board3DView(double width, double height) {
        setPrefSize(width, height);

        // Marco central del tablero
        Rectangle marcoTablero = new Rectangle(BOARD_WIDTH, GameView.BOARD_HEIGHT, GameView.COLOR.get("Board"));
        marcoTablero.setStroke(GameView.COLOR.get("BoardBorder"));
        marcoTablero.setStrokeWidth(BORDER_WIDTH);
        marcoTablero.setArcWidth(BOARD_CORNER_RADIUS);
        marcoTablero.setArcHeight(BOARD_CORNER_RADIUS);

        // Contenedor de las 6 filas
        VBox filasTablero = new VBox(BOARD_LINE_GAP);
        filasTablero.setLayoutX(BOARD_ROW_X);
        filasTablero.setLayoutY(BOARD_ROW_Y);

        filasTablero.getChildren().addAll(
            new RowView("Siege", GameView.COLOR.get("SiegeLine")),
            new RowView("Ranged", GameView.COLOR.get("RangedLine")),
            new RowView("Melee", GameView.COLOR.get("MeleeLine")),
            new RowView("Melee", GameView.COLOR.get("MeleeLine")),
            new RowView("Ranged", GameView.COLOR.get("RangedLine")),
            new RowView("Siege", GameView.COLOR.get("SiegeLine"))
        );

        Group superficieTablero = new Group(marcoTablero, filasTablero);
        superficieTablero.setLayoutX(BOARD_X);
        superficieTablero.setLayoutY(GameView.BOARD_Y);

        Group zonasLaterales = new Group(
            new BoardZoneView("DECK", GameView.ZONE_X, OPPONENT_DECK_Y, GameView.ZONE_WIDTH, GameView.ZONE_HEIGHT, GameView.COLOR.get("Deck")),
            new BoardZoneView("DISCARD", GameView.ZONE_X, OPPONENT_DISCARD_Y, GameView.ZONE_WIDTH, GameView.ZONE_HEIGHT, GameView.COLOR.get("Discard")),
            new BoardZoneView("Weather", GameView.ZONE_X, WEATHER_Y, GameView.ZONE_WIDTH, GameView.ZONE_HEIGHT, GameView.COLOR.get("Weather")),
            new BoardZoneView("DISCARD", GameView.ZONE_X, PLAYER_DISCARD_Y, GameView.ZONE_WIDTH, GameView.ZONE_HEIGHT, GameView.COLOR.get("Discard")),
            new BoardZoneView("DECK", GameView.ZONE_X, PLAYER_DECK_Y, GameView.ZONE_WIDTH, GameView.ZONE_HEIGHT, GameView.COLOR.get("Deck"))
        );


        // Gemas de vida en el tablero
        List<GemView> gems = new ArrayList<GemView>();
        gems.add(new GemView(GEM_X, OPPONENT_SECOND_GEM_Y));
        gems.add(new GemView(GEM_X, PLAYER_GEM_Y));
        gems.add(new GemView(GEM_X, OPPONENT_GEM_Y));
        gems.add(new GemView(GEM_X, PLAYER_SECOND_GEM_Y));
        
        
        /* new Thread(() -> {         // TESTING de destroyGem cada uno
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
        }).start(); */

        Group gemasGroup = new Group(gems.get(0), gems.get(1), gems.get(2), gems.get(3));

        

        tablero2_5d = new Group(superficieTablero, zonasLaterales);

        // Camara

        aplicarEfectoMesa();

        Rectangle limitesCamara = new Rectangle(
            CAMERA_CONTENT_WIDTH,
            CAMERA_CONTENT_HEIGHT,
            javafx.scene.paint.Color.TRANSPARENT
        );
        Group gemasCameraLayer = new Group(limitesCamara, gemasGroup);
        gemasCameraLayer.setManaged(false);
        gemasCameraLayer.setMouseTransparent(true);
        aplicarCamara(gemasCameraLayer);
        getChildren().addAll(tablero2_5d, gemasCameraLayer);
    }

    private void aplicarEfectoMesa() {
        aplicarCamara(tablero2_5d);
    }

    private void aplicarCamara(Node node) {
        double width = CAMERA_CONTENT_WIDTH;
        double height = CAMERA_CONTENT_HEIGHT;
        double topInset = width * CAMERA_TOP_MARGIN;
        double bottomInset = width * CAMERA_BOTTOM_MARGIN;

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

        Scale zoom = new Scale(
            GameView.CAMERA_ZOOM,
            GameView.CAMERA_ZOOM,
            width / 2,
            height / 2
        );
        node.getTransforms().add(zoom);
        node.setTranslateX(CAMERA_POSITION_X);
        node.setTranslateY(CAMERA_POSITION_Y);
        node.setLayoutY(BOARD_LAYOUT_Y);
    }
}