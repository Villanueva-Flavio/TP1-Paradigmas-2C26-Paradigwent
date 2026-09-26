package com.fiuba.paradigwent.view.game;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import java.util.Map;



import com.fiuba.paradigwent.view.View;
import com.fiuba.paradigwent.view.game.modules.Board3DView;

import java.util.HashMap;

public class GameView {

    public static final double VIRTUAL_WIDTH = 1000;
    public static final double VIRTUAL_HEIGHT = 600;
    public static final double CONTENT_SCALE = 1.0;
    public static final double MIN_VIEW_WIDTH = 640;
    public static final double MIN_VIEW_HEIGHT = 400;
    private static final double CONTENT_OFFSET_Y_RATIO = 0.0740740741;
    private static final double REFERENCE_WIDTH = 1920;
    private static final double REFERENCE_HEIGHT = 1080;
    private static final double REFERENCE_CONTENT_SCALE = REFERENCE_HEIGHT / VIRTUAL_HEIGHT;
    public static final double UI_OPPONENT_X = 465;
    public static final double UI_OPPONENT_Y = 75;
    public static final double UI_PLAYER_X = 465;
    public static final double UI_PLAYER_Y = 565;
    public static final double UI_FONT_SIZE = 15;
    public static final double BOARD_HEIGHT = 800;
    public static final double BOARD_Y = 20;
    public static final double ZONE_X = 890;
    public static final double ZONE_WIDTH = 60;
    public static final double ZONE_HEIGHT = 100;

    public static final Map<String, Color> COLOR = new HashMap<String, Color>() {{
        put("SiegeLine", Color.web("#d23737"));
        put("RangedLine", Color.web("#00ff00"));
        put("MeleeLine", Color.web("#5337d2"));
        put("Weather", Color.web("#e1a037"));
        put("Deck", Color.web("#a51f7d"));
        put("Discard", Color.web("#6d0e0e"));
        put("Board", Color.web("#3a4b68"));
        put("BoardBorder", Color.rgb(12, 15, 22, 0.88));
        put("BoardBackground", Color.rgb(18, 22, 32, 0.9));
        put("Gem", Color.web("#800f2f"));
        put("Row", Color.rgb(20, 26, 38, 0.85));
        put("DummyCard", Color.rgb(255, 255, 255, 0.15));
        put("DummyCardStroke", Color.web("#78a6ff"));
        put("DummyCardShadow", Color.BLACK);
        put("RowText", Color.web("#d0d6e0"));
    }};

    private StackPane rootContainer;
    private Pane capa0Fondo;
    private Board3DView capa2Plano3D;
    private Pane capa3UIOverlay;
    private Group contenidoEscalable;
    private Rectangle dimmer;

    public GameView(View mainView) {
        crearEstructuraCapas();
        construirCapa0Fondo();
        construirCapa3UI(mainView);
    }

    public Parent getRootNode() {
        return rootContainer;
    }

    private void crearEstructuraCapas() {
        rootContainer = new StackPane();
        rootContainer.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        rootContainer.setMinSize(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT);

        capa0Fondo = new Pane();
        capa2Plano3D = new Board3DView(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        capa3UIOverlay = new Pane();
        capa3UIOverlay.setPickOnBounds(false);

        contenidoEscalable = new Group(capa2Plano3D, capa3UIOverlay);
        contenidoEscalable.setManaged(false);

        dimmer = new Rectangle();
        dimmer.widthProperty().bind(rootContainer.widthProperty());
        dimmer.heightProperty().bind(rootContainer.heightProperty());
        dimmer.setFill(Color.rgb(0, 0, 0, 0.65));
        dimmer.setVisible(false);

        NumberBinding escala = Bindings.min(
            rootContainer.widthProperty().divide(REFERENCE_WIDTH),
            rootContainer.heightProperty().divide(REFERENCE_HEIGHT)
        ).multiply(REFERENCE_CONTENT_SCALE * CONTENT_SCALE);

        contenidoEscalable.scaleXProperty().bind(escala);
        contenidoEscalable.scaleYProperty().bind(escala);

        contenidoEscalable.layoutXProperty().bind(
            rootContainer.widthProperty().divide(2)
                .subtract(escala.multiply(VIRTUAL_WIDTH / 2))
        );
        contenidoEscalable.layoutYProperty().bind(
            rootContainer.heightProperty().multiply(CONTENT_OFFSET_Y_RATIO)
        );

        rootContainer.getChildren().addAll(capa0Fondo, contenidoEscalable, dimmer);
        rootContainer.widthProperty().addListener((observable, oldValue, newValue) ->
            Platform.runLater(this::centerBoardInViewport));
        rootContainer.heightProperty().addListener((observable, oldValue, newValue) ->
            Platform.runLater(this::centerBoardInViewport));
    }

    public void setDimmed(boolean dimmed) {
        dimmer.setVisible(dimmed);
        dimmer.setMouseTransparent(!dimmed);
    }

    public void centerBoardInViewport() {
        capa2Plano3D.centerInViewport(contenidoEscalable);
    }

    private void construirCapa0Fondo() {
        Rectangle fondoMesa = new Rectangle(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Color.web("#10131c"));
        fondoMesa.widthProperty().bind(rootContainer.widthProperty());
        fondoMesa.heightProperty().bind(rootContainer.heightProperty());

        capa0Fondo.getChildren().add(fondoMesa);
    }

    private void construirCapa3UI(View mainView) {
        Button btnBack = mainView.createMenuButton("Options");
        btnBack.setOnAction(e -> {
            mainView.showInGameConfigMenu();
        });
        btnBack.setLayoutX(VIRTUAL_WIDTH * 0.5);
        btnBack.setLayoutY(VIRTUAL_HEIGHT * 0.1666666667);
        capa3UIOverlay.getChildren().add(btnBack);
    }
}