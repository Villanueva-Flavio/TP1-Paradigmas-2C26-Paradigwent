package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.game.Game;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;
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

    public Board getBoard() {
        return boardController.getBoard();
    }

    public Game getGame() {
        return game;
    }

    public List<Card> getPlayerHand() {
        return player.getHand();
    }

    public int getOpponentHandSize() {
        return opponent.handSize();
    }

    public String getOpponentFaction() {
        return opponent.getFaction();
    }

    public boolean requiresTarget(Card card) {
        return card != null && card.requiresTarget();
    }

    public boolean playCard(Card card) {
        return playCard(card, null);
    }

    public boolean playCard(Card card, Card targetCard) {
        String rejection = validatePlayerPlay(card, targetCard);
        if (rejection != null) {
            GameStatePrinter.printRejected(rejection);
            return false;
        }

        round.playCard(card, targetCard);
        boardController.printSituation(player.getName(), card.getName());
        finishRoundIfNeeded();
        printCurrentTurn();
        return true;
    }

    private String validatePlayerPlay(Card card, Card targetCard) {
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
            if (targetCard == null) {
                return "el hechizo requiere una carta objetivo";
            }
            if (!getTargetCards().contains(targetCard)) {
                return "la carta objetivo no está en el tablero";
            }
        }
        return null;
    }

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
        if (card.requiresTarget() && target == null) {
            // There is no legal target for this card, so the AI passes.
            round.passTurn();
            finishRoundIfNeeded();
            printCurrentTurn();
            return false;
        }

        // Round creates a play context containing the selected card and target.
        // AI.play() recognizes the chosen card and delegates its execution to Player.
        round.playCard(card, target);
        lastOpponentCard = card;
        boardController.printSituation(opponent.getName(), card.getName());
        finishRoundIfNeeded();
        printCurrentTurn();
        return true;
    }

    private Card chooseOpponentTarget() {
        // Basic targeting policy: choose the first minion currently on the board.
        // This can later be replaced by a card-aware target-selection strategy.
        return getTargetCards().stream().findFirst().orElse(null);
    }

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
            GameStatePrinter.printRejected("la partida o la ronda ya terminó");
            return false;
        }
        if (round.getCurrentPlayer() != player) {
            GameStatePrinter.printRejected("no es el turno del jugador");
            return false;
        }

        round.passTurn();
        finishRoundIfNeeded();

        // If the human pass did not finish the round, the AI now owns the turn.
        // Run the AI turn immediately instead of leaving the game waiting.
        if (!game.isFinished()
                && !round.isFinished()
                && round.getCurrentPlayer() == opponent) {
            playOpponentCard();
        } else {
            printCurrentTurn();
        }

        // The action was accepted even if the round continues.
        return true;
    }

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

    private void printCurrentTurn() {
        if (!game.isFinished() && !round.isFinished()) {
            GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
        }
    }

    public void surrender() {
        if (game.isFinished()) {
            return;
        }
        game.surrender(player);
        GameStatePrinter.printSurrender(player.getName());
    }

    public boolean gameFinished() {
        return game.isFinished();
    }

    public Result getMatchResult() {
        return game.getMatchResult();
    }
}
