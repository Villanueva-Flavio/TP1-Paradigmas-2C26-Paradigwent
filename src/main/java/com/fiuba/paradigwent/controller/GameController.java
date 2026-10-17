package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.game.Game;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.view.game.modules.CardRace;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the human player's actions, the AI's decisions and the view.
 */
public final class GameController {

    private final Game game;
    private final BoardController boardController;
    private final Player player;
    private final AI opponent;
    private final CardRace opponentRace;

    private Round round;
    private Card lastOpponentCard;

    /**      * Constructs a new GameController.      */
    public GameController(CardRace selectedRace) {
        GameSessionFactory.Session session = new GameSessionFactory().create(selectedRace);
        game = session.game();
        player = session.player();
        opponent = session.opponent();
        opponentRace = session.opponentRace();
        round = session.round();
        boardController = new BoardController(game.getBoard());

        GameStatePrinter.printInitialSituation(game.getBoard());
        printCurrentTurn();
    }
    /**
     * Returns the value provided by getBoard.
     * @return the value produced by the method
     */
    public Board getBoard() {
        return boardController.getBoard();
    }
    /**
     * Returns the value provided by getPlayerHand.
     * @return the value produced by the method
     */
    public List<Card> getPlayerHand() {
        return player.getHand();
    }
    /**
     * Returns the value provided by getOpponentHandSize.
     * @return the value produced by the method
     */
    public int getOpponentHandSize() {
        return opponent.handSize();
    }
    /**
     * Returns the value provided by getOpponentFaction.
     * @return the value produced by the method
     */
    public String getOpponentFaction() {
        return opponent.getFaction();
    }
    /**
     * Returns the value provided by getCurrentPlayerName.
     * @return the value produced by the method
     */
    public String getCurrentPlayerName() {
        return round.getCurrentPlayer().getName();
    }
    /**
     * Reports the state represented by isPlayerTurn.
     * @return the value produced by the method
     */
    public boolean isPlayerTurn() {
        return round.getCurrentPlayer() == player;
    }
    /**
     * Executes the operation represented by requiresTarget.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public boolean requiresTarget(Card card) {
        return card != null && card.requiresTarget() && !getTargetCards().isEmpty();
    }
    /**
     * Executes the operation represented by playCard.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public boolean playCard(Card card) {
        return playCard(card, null);
    }
    /**
     * Executes the operation represented by playCard.
     * @param card the value used by the method
     * @param targetCard the value used by the method
     * @return the value produced by the method
     */
    public boolean playCard(Card card, Card targetCard) {
        return playCard(card, targetCard, null);
    }
    /**
     * Executes the operation represented by playCard.
     * @param card the value used by the method
     * @param targetCard the value used by the method
     * @param targetLine the value used by the method
     * @return the value produced by the method
     */
    public boolean playCard(Card card, Card targetCard, AttackLineType targetLine) {
        String rejection = validatePlayerPlay(card, targetCard, targetLine);
        if (rejection != null) {
            GameStatePrinter.printRejected(rejection);
            return false;
        }

        round.playCard(card, targetCard, targetLine);
        boardController.printSituation(player.getName(), card.getName());
        finishRoundIfNeeded();
        printCurrentTurn();
        return true;
    }
    /**
     * Executes the operation represented by validatePlayerPlay.
     * @param card the value used by the method
     * @param targetCard the value used by the method
     * @param targetLine the value used by the method
     * @return the value produced by the method
     */
    private String validatePlayerPlay(Card card, Card targetCard, AttackLineType targetLine) {
        if (card == null) {
            return "la carta es nula";
        }
        if (game.isFinished() || round.isFinished()) {
            return "la partida o la ronda ya terminó";
        }
        if (round.getCurrentPlayer() != player) {
            return "no es el turno del jugador";
        }
        if (!player.hasCard(card)) {
            return "la carta no está en la mano";
        }
        if (card.requiresTarget()) {
            boolean noTargetsAvailable = getTargetCards().isEmpty();
            if (targetCard == null && !isTargetOptional(card) && !noTargetsAvailable) {
                return "el hechizo requiere una carta objetivo";
            }
            if (targetCard != null && !getTargetCards().contains(targetCard)) {
                return "la carta objetivo no está en el tablero";
            }
        }
        if (requiresLineTarget(card)
                && (targetLine == null || targetLine == AttackLineType.NONE)) {
            return "la carta requiere una línea objetivo";
        }
        return null;
    }
    /**
     * Executes the operation represented by requiresLineTarget.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public boolean requiresLineTarget(Card card) {
        return card.getEffects().stream()
            .anyMatch(effect -> effect.isLineTargetEffect() && effect.requiresTarget());
    }
    /**
     * Reports the state represented by isTargetOptional.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public boolean isTargetOptional(Card card) {
        return card.hasOptionalTarget();
    }
    /**
     * Returns the value provided by getTargetCards.
     * @return the value produced by the method
     */
    public List<Card> getTargetCards() {
        return new ArrayList<>(getBoard().allMinions());
    }

    /**
     * Executes one AI turn. The AI chooses its card through AIStrategy;
     * RandomAIStrategy can return null to indicate that it wants to pass.
     */
    public boolean playOpponentCard() {
        lastOpponentCard = null;

        if (game.isFinished()
                || round.isFinished()
                || round.getCurrentPlayer() != opponent) {
            return false;
        }

        // Give the strategy the current board and both players, not a null context.
        GameContext decisionContext = new GameContext(
                opponent, player, getBoard(), null, null
        );
        Card card = opponent.selectCard(decisionContext);

        if (card == null || !opponent.hasCard(card)) {
            round.passTurn();
            finishRoundIfNeeded();
            printCurrentTurn();
            return false;
        }

        Card target = card.requiresTarget() ? chooseOpponentTarget() : null;
        if (card.requiresTarget() && target == null && !getTargetCards().isEmpty()) {

            round.passTurn();
            finishRoundIfNeeded();
            printCurrentTurn();
            return false;
        }

        round.playCard(card, target);
        lastOpponentCard = card;
        boardController.printSituation(opponent.getName(), card.getName());
        finishRoundIfNeeded();
        printCurrentTurn();
        return true;
    }
    /**
     * Executes the operation represented by chooseOpponentTarget.
     * @return the value produced by the method
     */
    private Card chooseOpponentTarget() {
        // Basic targeting policy: choose the first minion currently on the board.
        // This can later be replaced by a card-aware target-selection strategy.
        return getTargetCards().stream().findFirst().orElse(null);
    }
    /**
     * Returns the value provided by getLastOpponentCard.
     * @return the value produced by the method
     */
    public Card getLastOpponentCard() {
        return lastOpponentCard;
    }

    /**
     * Passes the human player's turn and immediately lets the AI take its turn
     * when the round is still active. Returns true when the pass was accepted,
     * not only when it happened to finish the round.
     */

    public boolean passTurn() {
        if (game.isFinished() || round.isFinished()) {
            GameStatePrinter.printRejected(
                    "la partida o la ronda ya terminó"
            );
            return false;
        }

        if (round.getCurrentPlayer() != player) {
            GameStatePrinter.printRejected(
                    "no es el turno del jugador"
            );
            return false;
        }

        round.passTurn();
        finishRoundIfNeeded();
        printCurrentTurn();

        return true;
    }
    /**
     * Executes the operation represented by playOpponentTurn.
     * @return the value produced by the method
     */
    public boolean playOpponentTurn() {
        if (!isOpponentTurn()) {
            return false;
        }

        playOpponentCard();
        return true;
    }
    /**
     * Reports the state represented by isOpponentTurn.
     * @return the value produced by the method
     */
    public boolean isOpponentTurn() {
        return !game.isFinished()
                && !round.isFinished()
                && round.getCurrentPlayer() == opponent;
    }
    /**
     * Executes the operation represented by finishRoundIfNeeded.
     * @return the value produced by the method
     */
    private boolean finishRoundIfNeeded() {
        if (!round.isFinished()) {
            return false;
        }

        Result result = round.getRoundResult();
        if (!game.resolveRound(round)) {
            return false;
        }

        System.out.printf("[GAME] Ronda finalizada: %s%n", result);
        System.out.printf(
                "[GAME] Vidas - %s: %d | %s (%s): %d%n",
                player.getName(), player.getLives(),
                opponent.getName(), opponentRace, opponent.getLives()
        );

        if (!game.isFinished()) {
            System.out.printf(
                    "[GAME] Nueva ronda: %s (mano=%d, mazo=%d); "
                            + "%s (mano=%d, mazo=%d)%n",
                    player.getName(), player.handSize(), player.getDeckSize(),
                    opponent.getName(), opponent.handSize(), opponent.getDeckSize()
            );

            // Round resets hasPassedRound for both players.
            round = new Round(player, opponent, game.getBoard());
        }

        return true;
    }
    /**
     * Displays the information handled by printCurrentTurn.
     */
    private void printCurrentTurn() {
        if (!game.isFinished() && !round.isFinished()) {
            GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
        }
    }
    /**
     * Executes the operation represented by surrender.
     */
    public void surrender() {
        if (game.isFinished()) {
            return;
        }
        game.surrender(player);
        GameStatePrinter.printSurrender(player.getName());
    }
    /**
     * Executes the operation represented by gameFinished.
     * @return the value produced by the method
     */
    public boolean gameFinished() {
        return game.isFinished();
    }
    /**
     * Returns the value provided by getMatchResult.
     * @return the value produced by the method
     */
    public Result getMatchResult() {
        return game.getMatchResult();
    }
}
