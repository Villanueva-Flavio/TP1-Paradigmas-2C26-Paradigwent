package com.fiuba.paradigwent.view.game;

import java.util.Random;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import com.fiuba.paradigwent.controller.GameController;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.event.GameListener;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.view.AudioManager;
import com.fiuba.paradigwent.view.MainView;
import com.fiuba.paradigwent.view.game.modules.Board3DView;
import com.fiuba.paradigwent.view.game.modules.GameTextures;
import com.fiuba.paradigwent.view.game.modules.HandView;
import com.fiuba.paradigwent.view.game.modules.OpponentHandView;

/**
 * Screen of a match. It builds the layers of the board, forwards the human's
 * actions to the controller and observes the match to animate what happens.
 * It never drives the match: turns and AI moves are decided by the model.
 */
public class GameView implements GameListener {

    public static final double VIRTUAL_WIDTH = 1000;
    public static final double VIRTUAL_HEIGHT = 600;
    private static final double CONTENT_SCALE = 1.0;
    private static final double MIN_VIEW_WIDTH = 640;
    private static final double MIN_VIEW_HEIGHT = 400;
    private static final double CONTENT_OFFSET_Y_RATIO = 0.0740740741;
    private static final double REFERENCE_WIDTH = 1920;
    private static final double REFERENCE_HEIGHT = 1080;
    private static final double REFERENCE_CONTENT_SCALE = REFERENCE_HEIGHT / VIRTUAL_HEIGHT;

    private final MainView mainView;
    private final AudioManager audioManager;
    private final GameTextures textures = new GameTextures();
    private final GameController controller;
    private StackPane rootContainer;
    private Pane backgroundLayer;
    private Board3DView boardView;
    private Pane uiOverlay;
    private Group scalableContent;
    private Group scalableOverlay;
    private HandView playerHandView;
    private OpponentHandView opponentHandView;
    private NumberBinding viewportScale;
    private CardPreviewOverlay preview;
    private CardFlightAnimation cardFlight;
    private ActionPrompt actionPrompt;
    private MatchStatusPanel statusPanel;
    private boolean resultShown;
    private boolean cardAnimationRunning;

    /**
     * Creates the screen and starts a new match.
     *
     * @param mainView        application window
     * @param selectedFaction faction chosen by the human
     */
    public GameView(MainView mainView, Faction selectedFaction) {
        this.mainView = mainView;
        this.audioManager = mainView.getCurrentAudioManager();
        this.controller = new GameController(selectedFaction, new DelayedTurnScheduler(new Random()));
        buildLayerStructure();
        buildBackground();
        buildUiControls();
        buildHands();
        controller.addListener(new GameSoundEffects(audioManager));
        controller.addListener(this);
        controller.startMatch();
    }

    /**
     * @return the root node of the screen
     */
    public Parent getRootNode() {
        return rootContainer;
    }

    /**
     * @return the weather of the board, used to resume the weather music
     */
    public WeatherType getCurrentWeather() {
        return controller.getCurrentWeather();
    }

    /**
     * Surrenders the match and shows the defeat.
     */
    public void surrender() {
        showMatchResult(Result.PLAYER2_WIN, "You surrendered. The match is over.");
        controller.surrender();
        refreshAll();
    }

    /**
     * Moves the board to the center of the window.
     */
    public void centerBoardInViewport() {
        boardView.centerInViewport(scalableContent);
    }

    // ---------------------------------------------------------------- layout

    private void buildLayerStructure() {
        rootContainer = new StackPane();
        rootContainer.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        rootContainer.setMinSize(MIN_VIEW_WIDTH, MIN_VIEW_HEIGHT);

        backgroundLayer = new Pane();
        boardView = new Board3DView(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, controller, textures);
        preview = new CardPreviewOverlay(rootContainer);
        cardFlight = new CardFlightAnimation(rootContainer);
        boardView.setCardHoverHandler(preview);
        uiOverlay = new Pane();
        uiOverlay.setManaged(false);
        uiOverlay.setPickOnBounds(false);

        scalableContent = createScalableGroup(boardView);
        scalableOverlay = createScalableGroup(uiOverlay);

        viewportScale = Bindings.min(rootContainer.widthProperty().divide(REFERENCE_WIDTH),
                rootContainer.heightProperty().divide(REFERENCE_HEIGHT))
            .multiply(REFERENCE_CONTENT_SCALE * CONTENT_SCALE);

        configureScaledGroup(scalableContent);
        configureScaledGroup(scalableOverlay);
        rootContainer.getChildren().addAll(backgroundLayer, scalableContent, scalableOverlay);
        rootContainer.widthProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(this::centerBoardInViewport));
        rootContainer.heightProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(this::centerBoardInViewport));
    }

    private Group createScalableGroup(Node content) {
        Group group = new Group(content);
        group.setManaged(false);
        return group;
    }

    private void configureScaledGroup(Group group) {
        Scale transform = new Scale();
        transform.setPivotX(0);
        transform.setPivotY(0);
        transform.xProperty().bind(viewportScale);
        transform.yProperty().bind(viewportScale);
        group.getTransforms().setAll(transform);
        group.layoutXProperty().bind(rootContainer.widthProperty().divide(2).subtract(viewportScale.multiply(VIRTUAL_WIDTH / 2)));
        group.layoutYProperty().bind(rootContainer.heightProperty().multiply(CONTENT_OFFSET_Y_RATIO));
    }

    private void buildBackground() {
        Image skybox = textures.load(GameTextures.SKYBOX);
        if (skybox == null) {
            Rectangle fallback = new Rectangle(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Color.web("#10131c"));
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
        statusPanel = new MatchStatusPanel(16, (VIRTUAL_HEIGHT - 190) / 2);
        actionPrompt = new ActionPrompt(VIRTUAL_WIDTH);
        uiOverlay.getChildren().addAll(statusPanel, actionPrompt);

        Button optionsButton = mainView.createMenuButton("Options");
        Button passButton = mainView.createMenuButton("PASS TURN");
        optionsButton.setOnAction(event -> mainView.showInGameConfigMenu());
        passButton.setOnAction(event -> passTurn());
        optionsButton.setMaxWidth(360);
        optionsButton.prefWidthProperty().bind(Bindings.min(360, rootContainer.widthProperty().multiply(0.28)));
        StackPane.setAlignment(optionsButton, Pos.TOP_RIGHT);
        StackPane.setMargin(optionsButton, new Insets(16, 16, 0, 0));
        passButton.setMaxWidth(220);
        StackPane.setAlignment(passButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(passButton, new Insets(0, 16, 16, 0));
        rootContainer.getChildren().addAll(optionsButton, passButton);
        refreshStatus();
        statusPanel.updateScores(controller);
    }

    private void buildHands() {
        playerHandView = new HandView(controller.getPlayerHand(), VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        playerHandView.setTranslateY(28);
        playerHandView.setOnPreviewRequested(preview::showHandCard);
        playerHandView.setOnPreviewCleared(preview::hide);
        playerHandView.setOnCardSelected(this::selectCard);
        opponentHandView = new OpponentHandView(0, 12);
        updateOpponentHand();
        uiOverlay.getChildren().addAll(playerHandView, opponentHandView);
        opponentHandView.toFront();
        audioManager.playCardDraw();
    }

    // ---------------------------------------------------------- human actions

    /**
     * The human picked a card: the card itself tells what has to be chosen.
     */
    private void selectCard(Card card) {
        if (cardAnimationRunning || !controller.isPlayerTurn()) return;
        cancelSelection();
        card.targeting().request(new HumanTargetSelector(
            card, controller.getTargetCandidates(), boardView, actionPrompt, this::playCard));
    }

    private void playCard(Card card, Minion target, AttackLineType line) {
        Point2D source = playerHandView.getCardCenterInScene(card);
        if (!controller.playCard(card, target, line)) return;
        cardAnimationRunning = true;
        playerHandView.refresh(controller.getPlayerHand());
        refreshStatus();
        cardFlight.play(card, false, source, () -> {
            cardAnimationRunning = false;
            refreshBoard();
        });
    }

    private void passTurn() {
        if (cardAnimationRunning) return;
        cancelSelection();
        controller.passTurn();
        refreshAll();
    }

    private void cancelSelection() {
        actionPrompt.hide();
        boardView.clearTargetSelection();
        boardView.clearLineSelection();
    }

    // ---------------------------------------------------------- match events

    @Override
    public void roundStarted(Player startingPlayer) {
        refreshAll();
        statusPanel.showNotice(startingPlayer.getName() + " starts the round");
    }

    @Override
    public void cardPlayed(Player player, Card card) {
        if (!controller.isOpponent(player)) return;
        cardFlight.play(card, true, opponentHandView.getDrawPointInScene(), this::refreshBoard);
    }

    @Override
    public void turnPassed(Player player) {
        refreshStatus();
        statusPanel.showNotice(player.getName() + " passed");
    }

    @Override
    public void cardsStolen(int amount) {
        playerHandView.refresh(controller.getPlayerHand());
        updateOpponentHand();
    }

    @Override
    public void roundFinished(Result result) {
        refreshAll();
        statusPanel.showNotice(switch (result) {
            case PLAYER1_WIN -> "You won the round";
            case PLAYER2_WIN -> "You lost the round";
            case DRAW -> "The round is a draw";
        });
    }

    @Override
    public void matchFinished(Result result) {
        refreshAll();
        showMatchResult(result, "The match is over.");
    }

    // --------------------------------------------------------------- refresh

    private void refreshAll() {
        playerHandView.refresh(controller.getPlayerHand());
        refreshBoard();
    }

    /**
     * Redraws the table and the scores together, so the score never shows a
     * card that is still flying or a card that already left the board.
     */
    private void refreshBoard() {
        preview.hide();
        boardView.refresh();
        updateOpponentHand();
        statusPanel.updateScores(controller);
        refreshStatus();
    }

    private void refreshStatus() {
        statusPanel.updateTurn(controller);
    }

    private void updateOpponentHand() {
        if (opponentHandView == null) return;
        opponentHandView.showHand(controller.getOpponentHandSize(), controller.getOpponentFaction().name());
        opponentHandView.setLayoutX((VIRTUAL_WIDTH - opponentHandView.getPrefWidth()) / 2);
    }

    private void showMatchResult(Result result, String detail) {
        if (resultShown) return;
        resultShown = true;
        cancelSelection();
        uiOverlay.getChildren().add(new MatchResultOverlay(
            result, detail, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, mainView::showMainMenu));
    }
}
