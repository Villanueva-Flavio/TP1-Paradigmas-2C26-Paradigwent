package com.fiuba.paradigwent.view.game;

import javafx.geometry.Point2D;
import javafx.geometry.Bounds;
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
import javafx.scene.control.Label;
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
import com.fiuba.paradigwent.view.game.modules.OpponentHandView;
import com.fiuba.paradigwent.view.game.modules.CardVisual;
import com.fiuba.paradigwent.view.game.modules.GameTextures;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private Pane backgroundLayer;
    private Board3DView boardView;
    private Pane uiOverlay;
    private Group scalableContent;
    private Group scalableOverlay;
    private HandView playerHandView;
    private OpponentHandView opponentHandView;
    private StackPane handPreview;
    private final GameController controller;
    private final MainView mainView;
    private NumberBinding viewportScale;
    private StackPane resultOverlay;
    private boolean cardAnimationRunning;
    private Card cardWaitingForTarget;
    private final Random animationRandom = new Random();

    public GameView(MainView mainView) {
        this(mainView, CardRace.HUMAN);
    }

    public GameView(MainView mainView, CardRace selectedRace) {
        this.mainView = mainView;
        controller = new GameController(selectedRace);
        buildLayerStructure();
        buildBackground();
        buildUiControls();
        buildHands();
    }

    public Parent getRootNode() {
        return rootContainer;
    }

    public void surrender() {
        controller.surrender();
        boardView.refresh();
        showMatchResult(Result.PLAYER2_WIN, "You surrendered. The match is over.");
    }

    private void buildLayerStructure() {
        rootContainer = new StackPane();
        rootContainer.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        rootContainer.setMinSize(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT);

        backgroundLayer = new Pane();
        boardView = new Board3DView(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, controller.getBoard());
        boardView.setCardHoverHandler(this::showBoardPreview);
        uiOverlay = createUiOverlay();

        scalableContent = createScalableGroup(boardView);
        scalableOverlay = createScalableGroup(uiOverlay);

        viewportScale = Bindings.min(rootContainer.widthProperty().divide(REFERENCE_WIDTH),rootContainer.heightProperty().divide(REFERENCE_HEIGHT))
                                .multiply(REFERENCE_CONTENT_SCALE * CONTENT_SCALE);

        configureScaledGroup(scalableContent);
        configureScaledGroup(scalableOverlay);
        rootContainer.getChildren().addAll(backgroundLayer, scalableContent, scalableOverlay);
        addViewportListeners();
    }

    public void centerBoardInViewport() {
        boardView.centerInViewport(scalableContent);
    }

    private void centerContentsInViewport() {
        centerBoardInViewport();
    }

    private Pane createUiOverlay() {
        Pane overlay = new Pane();
        overlay.setManaged(false);
        overlay.setPickOnBounds(false);
        return overlay;
    }

    private Group createScalableGroup(javafx.scene.Node content) {
        Group group = new Group(content);
        group.setManaged(false);
        return group;
    }

    private void configureScaledGroup(Group group) {
        javafx.scene.transform.Scale transform = new javafx.scene.transform.Scale();
        transform.setPivotX(0);
        transform.setPivotY(0);
        transform.xProperty().bind(viewportScale);
        transform.yProperty().bind(viewportScale);
        group.getTransforms().setAll(transform);
        group.layoutXProperty().bind(rootContainer.widthProperty().divide(2).subtract(viewportScale.multiply(VIRTUAL_WIDTH / 2)));
        group.layoutYProperty().bind(rootContainer.heightProperty().multiply(CONTENT_OFFSET_Y_RATIO));
    }

    private void addViewportListeners() {
        rootContainer.widthProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(this::centerContentsInViewport));
        rootContainer.heightProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(this::centerContentsInViewport));
    }

    private void buildBackground() {
        Image skybox = GameTextures.load(GameTextures.SKYBOX);
        if (skybox == null) {
            Rectangle fallback = new Rectangle(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,
                Color.web("#10131c"));
            fallback.widthProperty().bind(rootContainer.widthProperty());
            fallback.heightProperty().bind(rootContainer.heightProperty());
            backgroundLayer.getChildren().add(fallback);
            return;
        }

        ImageView skyboxView = new ImageView(skybox);
        skyboxView.setPreserveRatio(false);
        skyboxView.fitWidthProperty().bind(rootContainer.widthProperty());
        skyboxView.fitHeightProperty().bind(rootContainer.heightProperty());
        skyboxView.setMouseTransparent(true);
        backgroundLayer.getChildren().add(skyboxView);
    }

    private void buildUiControls() {
        Button optionsButton = mainView.createMenuButton("Options");
        Button passButton = mainView.createMenuButton("PASS TURN");
        optionsButton.setOnAction(event -> mainView.showInGameConfigMenu());
        passButton.setOnAction(event -> passTurn());
        configureTopRightButton(optionsButton, 360);
        configureBottomRightButton(passButton, 220);
        rootContainer.getChildren().addAll(optionsButton, passButton);
    }

    private void configureTopRightButton(Button button, double maxWidth) {
        button.setMaxWidth(maxWidth);
        button.prefWidthProperty().bind(Bindings.min(maxWidth, rootContainer.widthProperty().multiply(0.28)));
        StackPane.setAlignment(button, Pos.TOP_RIGHT);
        StackPane.setMargin(button, new Insets(16, 16, 0, 0));
    }

    private void configureBottomRightButton(Button button, double maxWidth) {
        button.setMaxWidth(maxWidth);
        StackPane.setAlignment(button, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(button, new Insets(0, 16, 16, 0));
    }

    private void buildHands() {
        List<Card> handCards = controller.getPlayerHand();
        playerHandView = new HandView(handCards, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        playerHandView.setTranslateY(28);
        playerHandView.setOnPreviewRequested(this::showHandPreview);
        playerHandView.setOnPreviewCleared(this::hidePreview);
        playerHandView.setOnCardSelected(this::playCard);
        opponentHandView = new OpponentHandView(0, 12);
        updateOpponentHand();
        uiOverlay.getChildren().addAll(playerHandView, opponentHandView);
        opponentHandView.toFront();
    }

    private void updateOpponentHand() {
        if (opponentHandView == null) return;
        opponentHandView.showHand(controller.getOpponentHandSize(), controller.getOpponentFaction());
        opponentHandView.setLayoutX((VIRTUAL_WIDTH - opponentHandView.getPrefWidth()) / 2);
    }

    private void playCard(Card card) {
        if (cardAnimationRunning) return;
        if (controller.requiresTarget(card)) {
            beginTargetSelection(card);
            return;
        }
        executeCard(card, null);
    }

    private void beginTargetSelection(Card card) {
        List<Card> targets = controller.getTargetCards();
        if (targets.isEmpty()) {
            showNoTargetAlert();
            return;
        }
        cardWaitingForTarget = card;
        boardView.setTargetSelection(targets, this::finishTargetSelection);
    }

    private void finishTargetSelection(Card target) {
        Card card = cardWaitingForTarget;
        cardWaitingForTarget = null;
        boardView.clearTargetSelection();
        if (card != null) {
            executeCard(card, target);
        }
    }

    private void executeCard(Card card, Card target) {
        Point2D playerSourcePoint = playerHandView.getCardCenterInScene(card);
        if (!controller.playCard(card, target)) return;
        cardAnimationRunning = true;
        playerHandView.removeCard(card);
        animateCardToBoard(card, false, playerSourcePoint, () -> {
            boardView.refresh();
            scheduleOpponentTurn();
        });
    }

    private void scheduleOpponentTurn() {
        int thinkingTime = 2000 + animationRandom.nextInt(3001);
        PauseTransition delay = new PauseTransition(Duration.millis(thinkingTime));
        delay.setOnFinished(event -> playOpponentTurn());
        delay.play();
    }

    private void playOpponentTurn() {
        Point2D sourcePoint = opponentHandView.getDrawPointInScene();
        if (!controller.playOpponentCard()) {
            finishCardAction();
            return;
        }

        Card opponentCard = controller.getLastOpponentCard();
        if (opponentCard == null) {
            finishCardAction();
            return;
        }

        updateOpponentHand();
        animateCardToBoard(opponentCard, true, sourcePoint, this::finishCardAction);
    }

    private void finishCardAction() {
        cardAnimationRunning = false;
        updateOpponentHand();
        boardView.refresh();
    }

    private void animateCardToBoard(Card card, boolean opponent, Point2D sourcePoint, Runnable onFinished) {
        CardVisual animatedCard = new CardVisual(card, CardVisual.widthForHeight(170), 170, "collection-card-image");
        configureAnimatedCard(animatedCard, card);
        rootContainer.getChildren().add(animatedCard);
        animatedCard.toFront();
        double cardWidth = animatedCard.getPrefWidth();
        double cardHeight = animatedCard.getPrefHeight();
        Point2D localSource = resolveAnimationSource(sourcePoint, opponent);
        double startX = localSource.getX() - cardWidth / 2;
        double startY = localSource.getY() - cardHeight / 2;
        double endY = rootContainer.getHeight() / 2 - animatedCard.getPrefHeight() / 2;
        animatedCard.relocate(startX, startY);
        double endX = rootContainer.getWidth() / 2 - cardWidth / 2;
        ParallelTransition launch = createCardLaunch(animatedCard, endX - startX, endY - startY, opponent);
        SequentialTransition animation = new SequentialTransition(new PauseTransition(Duration.millis(100)), launch, new PauseTransition(Duration.millis(250)));
        animation.setOnFinished(event -> {
            rootContainer.getChildren().remove(animatedCard);
            onFinished.run();
        });
        animation.play();
    }

    private void configureAnimatedCard(CardVisual visual, Card card) {
        visual.addMetadata(card);
        visual.setMouseTransparent(true);
        visual.setManaged(false);
        visual.setScaleX(0.72);
        visual.setScaleY(0.72);
        visual.setOpacity(0.95);
    }

    private Point2D resolveAnimationSource(Point2D sourcePoint, boolean opponent) {
        if (sourcePoint != null) return rootContainer.sceneToLocal(sourcePoint);
        return new Point2D(rootContainer.getWidth() / 2, opponent ? 80 : rootContainer.getHeight() - 80);
    }

    private ParallelTransition createCardLaunch(CardVisual visual, double moveX, double moveY, boolean opponent) {
        TranslateTransition move = new TranslateTransition(Duration.millis(650), visual);
        move.setToX(moveX);
        move.setToY(moveY);
        move.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scale = new ScaleTransition(Duration.millis(650), visual);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition rotate = new RotateTransition(Duration.millis(650), visual);
        rotate.setFromAngle(opponent ? -12 : 12);
        rotate.setToAngle(opponent ? -4 : 4);
        rotate.setInterpolator(Interpolator.EASE_OUT);

        return new ParallelTransition(move, scale, rotate);
    }

    private void showNoTargetAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Target required");
        alert.setHeaderText("No valid target cards");
        alert.setContentText("This spell cannot be played right now.");
        alert.showAndWait();
    }

    private void passTurn() {
        controller.passTurn();
        playerHandView.refresh(controller.getPlayerHand());
        updateOpponentHand();
        boardView.refresh();
        if (controller.gameFinished()) showMatchResult(controller.getMatchResult(), "The match is over.");
    }

    public void showMatchResult(Result result, String detail) {
        if (resultOverlay != null) return;
        resultOverlay = new StackPane();
        resultOverlay.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        resultOverlay.setPickOnBounds(true);
        resultOverlay.setStyle("-fx-background-color: rgba(5, 8, 16, 0.78);");
        resultOverlay.getChildren().add(createResultPanel(result, detail));
        uiOverlay.getChildren().add(resultOverlay);
    }

    private StackPane createResultPanel(Result result, String detail) {
        StackPane panel = new StackPane();
        panel.setPrefSize(420, 260);
        panel.setMaxSize(420, 260);
        panel.setStyle("-fx-background-color: rgba(25, 32, 50, 0.98);"
                    +  "-fx-border-color: #d8aa55; -fx-border-width: 3;"
                    +  "-fx-background-radius: 14; -fx-border-radius: 14;");

        panel.getChildren().addAll(createResultImagePlaceholder(), createResultTitle(result), createResultMessage(detail), createMainMenuButton());
        StackPane.setAlignment(panel, Pos.CENTER);
        return panel;
    }

    private Label createResultImagePlaceholder() {
        Label image = new Label("IMAGE / GIF");
        image.setPrefSize(180, 90);
        image.setAlignment(Pos.CENTER);
        image.setStyle("-fx-border-color: #71809f; -fx-border-width: 2;"
            + "-fx-text-fill: #9eacc8; -fx-font-size: 16px;");
        StackPane.setAlignment(image, Pos.TOP_CENTER);
        StackPane.setMargin(image, new Insets(20, 0, 0, 0));
        return image;
    }

    private Label createResultTitle(Result result) {
        Label title = new Label(getResultTitle(result));
        title.setStyle("-fx-text-fill: #f4d58d; -fx-font-size: 34px; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.CENTER);
        StackPane.setMargin(title, new Insets(88, 0, 0, 0));
        return title;
    }

    private Label createResultMessage(String detail) {
        Label message = new Label(detail);
        message.setStyle("-fx-text-fill: #e7eaf2; -fx-font-size: 16px;");
        StackPane.setAlignment(message, Pos.CENTER);
        StackPane.setMargin(message, new Insets(142, 0, 0, 0));
        return message;
    }

    private Button createMainMenuButton() {
        Button button = new Button("BACK TO MENU");
        button.setOnAction(event -> mainView.showMainMenu());
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setMargin(button, new Insets(0, 0, 20, 0));
        return button;
    }

    private String getResultTitle(Result result) {
        return switch (result) {
            case PLAYER1_WIN -> "¡VICTORY!";
            case PLAYER2_WIN -> "DEFEAT";
            case DRAW -> "DRAW";
        };
    }

    private void showHandPreview(Card card) {
        hidePreview();
        handPreview = new StackPane();
        handPreview.getStyleClass().add("collection-preview-overlay");
        handPreview.setPickOnBounds(false);
        Region backdrop = new Region();
        backdrop.setMouseTransparent(true);
        backdrop.setOpacity(0);
        backdrop.getStyleClass().add("collection-preview-backdrop");
        CardVisual preview = new CardVisual(card, CardVisual.widthForHeight(460), 460, "collection-card-image");
        preview.addMetadata(card);
        preview.setMouseTransparent(true);
        handPreview.getChildren().addAll(backdrop, preview);
        StackPane.setAlignment(preview, Pos.CENTER_RIGHT);
        StackPane.setMargin(preview, new Insets(0, 70, 0, 0));
        rootContainer.getChildren().add(handPreview);
    }

    private void showBoardPreview(Card card, javafx.scene.Node source) {
        if (card == null) {
            hidePreview();
            return;
        }
        showBoardPreviewAt(card, source);
    }

    private void showBoardPreviewAt(Card card, javafx.scene.Node source) {
        hidePreview();
        handPreview = new StackPane();
        handPreview.getStyleClass().add("collection-preview-overlay");
        handPreview.setPickOnBounds(false);
        Region backdrop = new Region();
        backdrop.setMouseTransparent(true);
        backdrop.setOpacity(0);
        backdrop.getStyleClass().add("collection-preview-backdrop");
        CardVisual preview = new CardVisual(card, CardVisual.widthForHeight(460), 460, "collection-card-image");
        preview.addMetadata(card);
        preview.setMouseTransparent(true);
        handPreview.getChildren().addAll(backdrop, preview);
        rootContainer.getChildren().add(handPreview);

        Bounds bounds = source.localToScene(source.getBoundsInLocal());
        Point2D point = rootContainer.sceneToLocal(bounds.getMinX() - preview.getPrefWidth() - 14, bounds.getMinY() - 10);
        double x = Math.max(8, Math.min(point.getX(), rootContainer.getWidth() - preview.getPrefWidth() - 8));
        double y = Math.max(8, Math.min(point.getY(), rootContainer.getHeight() - preview.getPrefHeight() - 8));
        preview.setManaged(false);
        preview.relocate(x, y);
    }

    private void hidePreview() {
        if (handPreview != null) {
            rootContainer.getChildren().remove(handPreview);
            handPreview = null;
        }
    }
}