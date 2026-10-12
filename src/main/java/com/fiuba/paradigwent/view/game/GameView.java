package com.fiuba.paradigwent.view.game;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.Map;
import java.util.List;

import com.fiuba.paradigwent.view.MainView;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.controller.GameController;
import com.fiuba.paradigwent.view.game.modules.Board3DView;
import com.fiuba.paradigwent.view.game.modules.CardRace;
import com.fiuba.paradigwent.view.game.modules.HandView;
import com.fiuba.paradigwent.view.game.modules.CardVisual;

import java.util.HashMap;

public class GameView {

    public static final double VIRTUAL_WIDTH = 1000;
    public static final double VIRTUAL_HEIGHT = 600;
    public static final double CONTENT_SCALE = 1.0;
    public static final double MIN_VIEW_WIDTH = 640;
    public static final double MIN_VIEW_HEIGHT = 400;
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
    
    private static final double CONTENT_OFFSET_Y_RATIO = 0.0740740741;
    private static final double REFERENCE_WIDTH = 1920;
    private static final double REFERENCE_HEIGHT = 1080;
    private static final double REFERENCE_CONTENT_SCALE = REFERENCE_HEIGHT / VIRTUAL_HEIGHT;

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
    private Group overlayEscalable;
    private HandView handLayer;
    private StackPane handPreview;
    private final GameController controller;
    private NumberBinding escalaViewport;

    public GameView(MainView mainView) {
        this(mainView, CardRace.HUMAN);
    }

    public GameView(MainView mainView, CardRace selectedRace) {
        controller = new GameController(selectedRace);
        crearEstructuraCapas();
        construirCapa0Fondo();
        construirCapa3UI(mainView);
        construirMano();
    }

    public Parent getRootNode() {
        return rootContainer;
    }

    public void surrender() {
        controller.surrender();
    }

    private void crearEstructuraCapas() {
        rootContainer = new StackPane();
        rootContainer.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        rootContainer.setMinSize(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT);

        capa0Fondo = new Pane();
        capa2Plano3D = new Board3DView(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, controller.getBoard());
        capa3UIOverlay = new Pane();
        capa3UIOverlay.setManaged(false);
        capa3UIOverlay.setPickOnBounds(false);

        contenidoEscalable = new Group(capa2Plano3D);
        contenidoEscalable.setManaged(false);
        overlayEscalable = new Group(capa3UIOverlay);
        overlayEscalable.setManaged(false);

        escalaViewport = Bindings.min(
            rootContainer.widthProperty().divide(REFERENCE_WIDTH),
            rootContainer.heightProperty().divide(REFERENCE_HEIGHT)
        ).multiply(REFERENCE_CONTENT_SCALE * CONTENT_SCALE);

        javafx.scene.transform.Scale scaleTransform = new javafx.scene.transform.Scale();
        scaleTransform.setPivotX(0);
        scaleTransform.setPivotY(0);
        scaleTransform.xProperty().bind(escalaViewport);
        scaleTransform.yProperty().bind(escalaViewport);

        contenidoEscalable.getTransforms().setAll(scaleTransform);
        overlayEscalable.getTransforms().setAll(scaleTransform);

        // Posicionamiento horizontal centrado para ambas capas
        contenidoEscalable.layoutXProperty().bind(
            rootContainer.widthProperty().divide(2)
                .subtract(escalaViewport.multiply(VIRTUAL_WIDTH / 2))
        );
        contenidoEscalable.layoutYProperty().bind(
            rootContainer.heightProperty().multiply(CONTENT_OFFSET_Y_RATIO)
        );

        overlayEscalable.layoutXProperty().bind(
            rootContainer.widthProperty().divide(2)
                .subtract(escalaViewport.multiply(VIRTUAL_WIDTH / 2))
        );
        overlayEscalable.layoutYProperty().bind(
            rootContainer.heightProperty().multiply(CONTENT_OFFSET_Y_RATIO)
        );

        rootContainer.getChildren().addAll(capa0Fondo, contenidoEscalable, overlayEscalable);
        rootContainer.widthProperty().addListener((observable, oldValue, newValue) ->
            Platform.runLater(this::centerContentsInViewport));
        rootContainer.heightProperty().addListener((observable, oldValue, newValue) ->
            Platform.runLater(this::centerContentsInViewport));
    }

    public void centerBoardInViewport() {
        capa2Plano3D.centerInViewport(contenidoEscalable);
    }

    private void centerContentsInViewport() {
        centerBoardInViewport();
    }

    private void construirCapa0Fondo() {
        Rectangle fondoMesa = new Rectangle(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Color.web("#10131c"));
        fondoMesa.widthProperty().bind(rootContainer.widthProperty());
        fondoMesa.heightProperty().bind(rootContainer.heightProperty());

        capa0Fondo.getChildren().add(fondoMesa);
    }

    private void construirCapa3UI(MainView mainView) {
        Button btnBack = mainView.createMenuButton("Options");
        btnBack.setOnAction(e -> {
            mainView.showInGameConfigMenu();
        });
        btnBack.setMaxWidth(360);
        btnBack.prefWidthProperty().bind(Bindings.min(
            360, rootContainer.widthProperty().multiply(0.28)));
        StackPane.setAlignment(btnBack, Pos.TOP_RIGHT);
        StackPane.setMargin(btnBack, new Insets(16, 16, 0, 0));
        rootContainer.getChildren().add(btnBack);
        Button passButton = mainView.createMenuButton("PASS TURN");
        passButton.setOnAction(event -> passTurn());
        passButton.setMaxWidth(220);
        StackPane.setAlignment(passButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(passButton, new Insets(0, 16, 16, 0));
        rootContainer.getChildren().add(passButton);
    }

    private void construirMano() {
        List<Card> handCards = controller.getPlayerHand();
        handLayer = new HandView(handCards, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        handLayer.setOnPreviewRequested(this::showHandPreview);
        handLayer.setOnPreviewCleared(this::hideHandPreview);
        handLayer.setOnCardSelected(this::playCard);
        capa3UIOverlay.getChildren().add(handLayer);
    }

    private void playCard(Card card) {
        if (!controller.playCard(card)) {
            return;
        }
        handLayer.removeCard(card);
        capa2Plano3D.refresh();
        PauseTransition opponentDelay = new PauseTransition(Duration.millis(360));
        opponentDelay.setOnFinished(event -> {
            if (controller.playOpponentCard()) {
                capa2Plano3D.refresh();
            }
        });
        opponentDelay.play();
    }

    private void passTurn() {
        if (controller.passTurn()) {
            handLayer.refresh(controller.getPlayerHand());
            capa2Plano3D.refresh();
        }
    }

    private void showHandPreview(Card card) {
        hideHandPreview();
        handPreview = new StackPane();
        handPreview.getStyleClass().add("collection-preview-overlay");
        handPreview.setPickOnBounds(false);
        Region backdrop = new Region();
        backdrop.setMouseTransparent(true);
        backdrop.setOpacity(0);
        backdrop.getStyleClass().add("collection-preview-backdrop");
        CardVisual preview = new CardVisual(card, 460 / 1.46, 460, "collection-card-image");
        preview.addMetadata(card);
        preview.setMouseTransparent(true);
        handPreview.getChildren().addAll(backdrop, preview);
        StackPane.setAlignment(preview, Pos.CENTER_RIGHT);
        StackPane.setMargin(preview, new Insets(0, 70, 0, 0));
        rootContainer.getChildren().add(handPreview);
    }

    private void hideHandPreview() {
        if (handPreview != null) {
            rootContainer.getChildren().remove(handPreview);
            handPreview = null;
        }
    }
}