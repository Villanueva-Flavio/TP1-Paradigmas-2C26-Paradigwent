package com.fiuba.paradigwent.view.game;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import java.util.Map;
import java.util.List;
import java.util.Random;

import com.fiuba.paradigwent.view.MainView;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Result;
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
    private final MainView mainView;
    private NumberBinding escalaViewport;
    private StackPane resultOverlay;
    private boolean cardAnimationRunning;
    private final Random animationRandom = new Random();

    public GameView(MainView mainView) {
        this(mainView, CardRace.HUMAN);
    }

    public GameView(MainView mainView, CardRace selectedRace) {
        this.mainView = mainView;
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
        capa2Plano3D.refresh();
        showMatchResult(Result.PLAYER2_WIN, "Te rendiste. La partida terminó.");
    }

    private void crearEstructuraCapas() {
        rootContainer = new StackPane();
        rootContainer.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        rootContainer.setMinSize(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT);

        capa0Fondo = new Pane();
        capa2Plano3D = new Board3DView(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, controller.getBoard());
        capa2Plano3D.setCardHoverHandler(this::showBoardPreview);
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
        if (cardAnimationRunning) {
            return;
        }
        Card target = null;
        if (controller.requiresTarget(card)) {
            target = chooseTarget(card);
            if (target == null) {
                return;
            }
        }
        if (!controller.playCard(card, target)) return;
        cardAnimationRunning = true;
        handLayer.removeCard(card);
        animateCardToBoard(card, false, () -> {
            capa2Plano3D.refresh();
            int thinkingTime = 2000 + animationRandom.nextInt(3001);
            PauseTransition opponentDelay = new PauseTransition(
                Duration.millis(thinkingTime));
            opponentDelay.setOnFinished(event -> {
                if (!controller.playOpponentCard()) {
                    cardAnimationRunning = false;
                    capa2Plano3D.refresh();
                    return;
                }
                Card opponentCard = controller.getLastOpponentCard();
                if (opponentCard == null) {
                    cardAnimationRunning = false;
                    capa2Plano3D.refresh();
                    return;
                }
                animateCardToBoard(opponentCard, true, () -> {
                    capa2Plano3D.refresh();
                    cardAnimationRunning = false;
                });
            });
            opponentDelay.play();
        });
    }

    private void animateCardToBoard(Card card, boolean opponent, Runnable onFinished) {
        CardVisual animatedCard = new CardVisual(card, CardVisual.widthForHeight(170), 170,
            "collection-card-image");
        animatedCard.addMetadata(card);
        animatedCard.setMouseTransparent(true);
        animatedCard.setManaged(false);
        animatedCard.setScaleX(1.0);
        animatedCard.setScaleY(1.0);
        animatedCard.setOpacity(0.15);
        rootContainer.getChildren().add(animatedCard);
        animatedCard.toFront();

        double cardWidth = animatedCard.getPrefWidth();
        double cardHeight = animatedCard.getPrefHeight();
        double startX = opponent
            ? rootContainer.getWidth() - cardWidth - 70
            : 70;
        double startY = opponent
            ? -cardHeight - 35
            : rootContainer.getHeight() + 35;
        double endY = rootContainer.getHeight() / 2 - animatedCard.getPrefHeight() / 2;
        animatedCard.relocate(startX, startY);

        double endX = rootContainer.getWidth() / 2 - cardWidth / 2;
        double moveX = endX - startX;
        double moveY = endY - startY;

        FadeTransition fade = new FadeTransition(Duration.millis(250), animatedCard);
        fade.setToValue(1);
        TranslateTransition move = new TranslateTransition(Duration.millis(200), animatedCard);
        move.setFromY(0);
        move.setToY(moveY);
        move.setFromX(0);
        move.setToX(moveX);
        move.setInterpolator(Interpolator.EASE_OUT);
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), animatedCard);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);
        RotateTransition rotate = new RotateTransition(Duration.millis(200), animatedCard);
        rotate.setFromAngle(opponent ? 18 : -18);
        rotate.setToAngle(opponent ? -4 : 4);
        rotate.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition launch = new ParallelTransition(move, scale, rotate);
        SequentialTransition animation = new SequentialTransition(
            new PauseTransition(Duration.millis(500)),
            fade,
            launch,
            new PauseTransition(Duration.millis(500)));
        animation.setOnFinished(event -> {
            rootContainer.getChildren().remove(animatedCard);
            onFinished.run();
        });
        animation.play();
    }

    private Card chooseTarget(Card spell) {
        List<Card> targets = controller.getTargetCards();
        if (targets.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Objetivo requerido");
            alert.setHeaderText("No hay cartas objetivo válidas");
            alert.setContentText("Este hechizo no puede jugarse en este momento.");
            alert.showAndWait();
            return null;
        }
        ChoiceDialog<Card> dialog = new ChoiceDialog<>(targets.get(0), targets);
        dialog.setTitle("Elegir objetivo");
        dialog.setHeaderText("Seleccioná una carta para " + spell.getName()
            + "\n" + spell.getTargetDescription());
        dialog.setContentText("Carta objetivo:");
        return dialog.showAndWait().orElse(null);
    }

    private void passTurn() {
        controller.passTurn();
        handLayer.refresh(controller.getPlayerHand());
        capa2Plano3D.refresh();
        if (controller.gameFinished()) {
            showMatchResult(controller.getMatchResult(), "La partida terminó.");
        }
    }

    public void showMatchResult(Result result, String detail) {
        if (resultOverlay != null) {
            return;
        }

        resultOverlay = new StackPane();
        resultOverlay.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        resultOverlay.setPickOnBounds(true);
        resultOverlay.setStyle("-fx-background-color: rgba(5, 8, 16, 0.78);");

        StackPane panel = new StackPane();
        panel.setPrefSize(420, 260);
        panel.setMaxSize(420, 260);
        panel.setStyle("-fx-background-color: rgba(25, 32, 50, 0.98);"
            + "-fx-border-color: #d8aa55; -fx-border-width: 3;"
            + "-fx-background-radius: 14; -fx-border-radius: 14;");

        Label imageSlot = new Label("IMAGEN / GIF");
        imageSlot.setPrefSize(180, 90);
        imageSlot.setAlignment(Pos.CENTER);
        imageSlot.setStyle("-fx-border-color: #71809f; -fx-border-width: 2;"
            + "-fx-text-fill: #9eacc8; -fx-font-size: 16px;");
        StackPane.setAlignment(imageSlot, Pos.TOP_CENTER);
        StackPane.setMargin(imageSlot, new Insets(20, 0, 0, 0));

        Label title = new Label(resultTitle(result));
        title.setStyle("-fx-text-fill: #f4d58d; -fx-font-size: 34px; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.CENTER);
        StackPane.setMargin(title, new Insets(88, 0, 0, 0));

        Label message = new Label(detail);
        message.setStyle("-fx-text-fill: #e7eaf2; -fx-font-size: 16px;");
        StackPane.setAlignment(message, Pos.CENTER);
        StackPane.setMargin(message, new Insets(142, 0, 0, 0));

        Button menuButton = new Button("VOLVER AL MENÚ");
        menuButton.setOnAction(event -> mainView.showMainMenu());
        menuButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane.setAlignment(menuButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(menuButton, new Insets(0, 0, 20, 0));

        panel.getChildren().addAll(imageSlot, title, message, menuButton);
        StackPane.setAlignment(panel, Pos.CENTER);
        resultOverlay.getChildren().add(panel);
        capa3UIOverlay.getChildren().add(resultOverlay);
    }

    private String resultTitle(Result result) {
        return switch (result) {
            case PLAYER1_WIN -> "¡VICTORIA!";
            case PLAYER2_WIN -> "DERROTA";
            case DRAW -> "EMPATE";
        };
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
        CardVisual preview = new CardVisual(card, CardVisual.widthForHeight(460), 460,
            "collection-card-image");
        preview.addMetadata(card);
        preview.setMouseTransparent(true);
        handPreview.getChildren().addAll(backdrop, preview);
        StackPane.setAlignment(preview, Pos.CENTER_RIGHT);
        StackPane.setMargin(preview, new Insets(0, 70, 0, 0));
        rootContainer.getChildren().add(handPreview);
    }

    private void showBoardPreview(Card card, javafx.scene.Node source) {
        if (card == null) {
            hideHandPreview();
            return;
        }
        showBoardPreviewAt(card, source);
    }

    private void showBoardPreviewAt(Card card, javafx.scene.Node source) {
        hideHandPreview();
        handPreview = new StackPane();
        handPreview.getStyleClass().add("collection-preview-overlay");
        handPreview.setPickOnBounds(false);
        Region backdrop = new Region();
        backdrop.setMouseTransparent(true);
        backdrop.setOpacity(0);
        backdrop.getStyleClass().add("collection-preview-backdrop");
        CardVisual preview = new CardVisual(card, CardVisual.widthForHeight(460), 460,
            "collection-card-image");
        preview.addMetadata(card);
        preview.setMouseTransparent(true);
        handPreview.getChildren().addAll(backdrop, preview);
        rootContainer.getChildren().add(handPreview);

        javafx.geometry.Bounds bounds = source.localToScene(source.getBoundsInLocal());
        javafx.geometry.Point2D point = rootContainer.sceneToLocal(
            bounds.getMinX() - preview.getPrefWidth() - 14,
            bounds.getMinY() - 10);
        double x = Math.max(8, Math.min(point.getX(),
            rootContainer.getWidth() - preview.getPrefWidth() - 8));
        double y = Math.max(8, Math.min(point.getY(),
            rootContainer.getHeight() - preview.getPrefHeight() - 8));
        preview.setManaged(false);
        preview.relocate(x, y);
    }

    private void hideHandPreview() {
        if (handPreview != null) {
            rootContainer.getChildren().remove(handPreview);
            handPreview = null;
        }
    }
}