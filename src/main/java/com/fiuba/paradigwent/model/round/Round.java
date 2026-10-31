package com.fiuba.paradigwent.model.round;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.event.EffectListener;
import com.fiuba.paradigwent.model.player.Player;

/**
 * A round between two players. It controls whose turn it is, lets the
 * current player play a card or pass, and knows when the round is over and
 * who won it.
 */
public class Round {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private final EffectListener listener;
    private Player currentPlayer;
    private boolean finished;

    /**
     * Creates a round opened by the first player and without observers.
     *
     * @param player1 first player
     * @param player2 second player
     * @param board   board of the match
     */
    public Round(Player player1, Player player2, Board board) {
        this(player1, player2, board, player1, new EffectListener() { });
    }

    /**
     * Creates a round and resets the round state of both players.
     *
     * @param player1        first player
     * @param player2        second player
     * @param board          board of the match
     * @param startingPlayer player that takes the first turn
     * @param listener       observer of the effects resolved during the round
     */
    public Round(Player player1, Player player2, Board board, Player startingPlayer, EffectListener listener) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
        this.listener = listener;
        this.currentPlayer = startingPlayer;
        this.finished = false;

        this.player1.startRound();
        this.player2.startRound();
    }

    /**
     * Plays a card without target on behalf of the current player.
     *
     * @param card the card to be played
     */
    public void playCard(Card card) {
        playCard(card, null, null);
    }

    /**
     * Plays a card on a minion on behalf of the current player.
     *
     * @param card   the card to be played
     * @param target chosen minion, may be {@code null}
     */
    public void playCard(Card card, Minion target) {
        playCard(card, target, null);
    }

    /**
     * Plays a card on behalf of the current player and advances the turn.
     * Nothing happens if the round is over or the current player passed.
     *
     * @param card       the card to be played
     * @param target     chosen minion, may be {@code null}
     * @param targetLine chosen line, may be {@code null}
     */
    public void playCard(Card card, Minion target, AttackLineType targetLine) {
        if (finished || currentPlayer.hasPassedRound()) {
            return;
        }

        GameContext context = new GameContext(
            currentPlayer, getOpponent(currentPlayer), board, listener, card, target, targetLine);
        currentPlayer.play(card, context);

        advanceTurn();
    }

    /**
     * Makes the current player pass the round and advances the turn.
     * Nothing happens if the round is over or the current player passed.
     */
    public void passTurn() {
        if (finished || currentPlayer.hasPassedRound()) {
            return;
        }

        currentPlayer.passRound();
        advanceTurn();
    }

    /**
     * Gives the turn to the opponent unless it already passed. The round
     * finishes when both players passed.
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
     * Compares the power of both players.
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
     * @param player a player of the round
     * @return the other player
     */
    public Player getOpponent(Player player) {
        return (player == player1) ? player2 : player1;
    }

    /**
     * @return the player who currently has the turn
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @param player a player of the round
     * @return {@code true} if the round is running and it is that player's turn
     */
    public boolean isTurnOf(Player player) {
        return !finished && currentPlayer == player;
    }

    /**
     * @return {@code true} if both players passed and the round is over
     */
    public boolean isFinished() {
        return finished;
    }
}
