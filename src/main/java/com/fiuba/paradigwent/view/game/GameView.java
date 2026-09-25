package com.fiuba.paradigwent.view.game;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.Map;

import com.fiuba.paradigwent.view.game.modules.Board3DView;

import java.util.HashMap;

public class GameView {

    public static final double VIRTUAL_WIDTH = 1000;
    public static final double VIRTUAL_HEIGHT = 600;
    public static final double CONTENT_SCALE = 1.0;
    public static final double MIN_VIEW_WIDTH = 640;
    public static final double MIN_VIEW_HEIGHT = 400;
    public static final double CONTENT_OFFSET_X = 400;
    public static final double CONTENT_OFFSET_Y = 80;
    public static final double CAMERA_ZOOM = 0.70;
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

    public GameView() {
        crearEstructuraCapas();
        construirCapa0Fondo();
        construirCapa3UI();
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

        NumberBinding escala = Bindings.min(
            rootContainer.widthProperty().divide(VIRTUAL_WIDTH),
            rootContainer.heightProperty().divide(VIRTUAL_HEIGHT)
        ).multiply(CONTENT_SCALE);

        contenidoEscalable.scaleXProperty().bind(escala);
        contenidoEscalable.scaleYProperty().bind(escala);
        contenidoEscalable.setTranslateX(CONTENT_OFFSET_X);
        contenidoEscalable.setTranslateY(CONTENT_OFFSET_Y);

        contenidoEscalable.layoutXProperty().bind(
            rootContainer.widthProperty().subtract(escala.multiply(VIRTUAL_WIDTH)).divide(2)
        );
        contenidoEscalable.layoutYProperty().bind(
            rootContainer.heightProperty().subtract(escala.multiply(VIRTUAL_HEIGHT)).divide(2)
        );

        rootContainer.getChildren().addAll(capa0Fondo, contenidoEscalable);
    }

    private void construirCapa0Fondo() {
        Rectangle fondoMesa = new Rectangle(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Color.web("#10131c"));
        fondoMesa.widthProperty().bind(rootContainer.widthProperty());
        fondoMesa.heightProperty().bind(rootContainer.heightProperty());

        capa0Fondo.getChildren().add(fondoMesa);
    }

    private void construirCapa3UI() {
        Text txtOponente = new Text("ENEMY");
        txtOponente.setFont(Font.font("System", FontWeight.BOLD, UI_FONT_SIZE));
        txtOponente.setFill(Color.web("#e63946"));
        txtOponente.setLayoutX(UI_OPPONENT_X);
        txtOponente.setLayoutY(UI_OPPONENT_Y);

        Text txtJugador = new Text("PLAYER");
        txtJugador.setFont(Font.font("System", FontWeight.BOLD, UI_FONT_SIZE));
        txtJugador.setFill(Color.web("#457b9d"));
        txtJugador.setLayoutX(UI_PLAYER_X);
        txtJugador.setLayoutY(UI_PLAYER_Y);

        capa3UIOverlay.getChildren().addAll(txtOponente, txtJugador);
    }
}