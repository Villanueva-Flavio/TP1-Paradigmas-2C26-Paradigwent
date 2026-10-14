package com.fiuba.paradigwent.model.round;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Represents and manages a round of the game between two players on a board.
 * It controls the current turn, allows playing cards or passing the round,
 * and determines when the round is finished and its result.
 */
public class Round {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private Player currentPlayer;
    private boolean finished;

    /**
     * Constructs a new Round between two players on a given board.
     * Initializes the turn to player1 and enables both players to start the round.
     *
     * @param player1 the first player of the round
     * @param player2 the second player of the round
     * @param board the board on which the round takes place
     */
    public Round(Player player1, Player player2, Board board) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
        this.currentPlayer = player1;
        this.finished = false;

        this.player1.startRound();
        this.player2.startRound();
    }

    /**
     * Plays a card on behalf of the current player.
     * If the round is already finished or the current player has passed, no action is taken.
     * It creates the game context and delegates the card execution to the current player.
     * Then it advances the turn.
     *
     * @param card the card to be played
     */
    public void playCard(Card card) {
        playCard(card, null);
    }

    public void playCard(Card card, Card targetCard) {
        if (finished || currentPlayer.hasPassedRound()) {
            return;
        }

        Player opponent = getOpponent(currentPlayer);

        GameContext context = new GameContext(currentPlayer, opponent, board, card, targetCard);

        currentPlayer.play(context);

        advanceTurn();
    }

    /**
     * Makes the current player pass the round.
     * If the round is already finished or the current player has passed, no action is taken.
     * Then it advances the turn.
     */
    public void passTurn() {
        if (finished || currentPlayer.hasPassedRound()) {
            return;
        }

        currentPlayer.passRound();
        advanceTurn();
    }

    /**
     * Advances the turn to the opponent if they have not passed yet.
     * If both players have passed, marks the round as finished.
     * If the opponent has already passed, the current player keeps the turn.
     */
    private void advanceTurn() {
        if (player1.hasPassedRound() && player2.hasPassedRound()) {
            this.finished = true;
            return;
        }

        Player opponent = getOpponent(currentPlayer);

        if (!opponent.hasPassedRound()) {
            this.currentPlayer = opponent;
        }
    }

    /**
     * Calculates and returns the result of the round by comparing the total power
     * of both players on the board.
     *
     * @return the round result: player 1 wins, player 2 wins, or draw
     * @throws IllegalStateException if the round is not finished yet
     */
    public Result getRoundResult() {
        if (!finished) {
            throw new IllegalStateException("Cannot evaluate the result of an ongoing round.");
        }

        int power1 = board.calculateTotalPower(player1);
        int power2 = board.calculateTotalPower(player2);

        if (power1 > power2) {
            return Result.PLAYER1_WIN;
        } else if (power2 > power1) {
            return Result.PLAYER2_WIN;
        } else {
            return Result.DRAW;
        }
    }

    /**
     * Returns the opponent of the given player.
     *
     * @param player the player whose opponent is requested
     * @return the other player in the round
     */
    public Player getOpponent(Player player) {
        return (player == player1) ? player2 : player1;
    }

    /**
     * Returns the player who currently has the turn.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Indicates whether the round has finished.
     *
     * @return {@code true} if both players passed and the round is over;
     *         {@code false} otherwise
     */
    public boolean isFinished() {
        return finished;
    }
}