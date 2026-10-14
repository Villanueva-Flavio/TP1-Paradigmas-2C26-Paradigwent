package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;

/**
 * Maintains the match state and resolves completed rounds.
 * Turn-by-turn decisions are coordinated by GameController.
 */
public class Game {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private boolean finished;
    private Player winner;

    public Game(Player player1, Player player2, Board board) {
        if (player1 == null || player2 == null || board == null) {
            throw new IllegalArgumentException(
                    "Los jugadores y el tablero no pueden ser nulos."
            );
        }
        if (player1 == player2) {
            throw new IllegalArgumentException(
                    "Los jugadores deben ser distintos."
            );
        }

        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
        this.finished = false;
        this.winner = null;
    }

    /**
     * Initializes match-level state.
     * Round is responsible for resetting each player's round state.
     */
    public void startMatch() {
        finished = false;
        winner = null;
    }

    /**
     * Resolves a completed round, applies life loss, clears the board and
     * draws cards for the next round if the match continues.
     *
     * @return true when the supplied round was resolved, false otherwise.
     */
    public boolean resolveRound(Round round) {
        if (finished || round == null || !round.isFinished()) {
            return false;
        }

        applyRoundResult(round.getRoundResult());
        board.discard();
        updateMatchState();

        return true;
    }

    private void applyRoundResult(Result result) {
        switch (result) {
            case PLAYER1_WIN -> player2.loseLife();
            case PLAYER2_WIN -> player1.loseLife();
            case DRAW -> {
                player1.loseLife();
                player2.loseLife();
            }
        }
    }

    private void updateMatchState() {
        finished = player1.getLives() <= 0 || player2.getLives() <= 0;

        if (!finished) {
            winner = null;
        } else if (player1.getLives() > player2.getLives()) {
            winner = player1;
        } else if (player2.getLives() > player1.getLives()) {
            winner = player2;
        } else {
            winner = null;
        }
    }

    public void surrender(Player player) {
        if (finished) {
            return;
        }
        if (player != player1 && player != player2) {
            throw new IllegalArgumentException(
                    "El jugador no pertenece a esta partida."
            );
        }

        winner = (player == player1) ? player2 : player1;
        while (player.getLives() > 0) {
            player.loseLife();
        }
        finished = true;
    }

    /**
     * Returns the winner once the match is finished.
     * A null result means the match ended in a draw.
     */
    public Player checkMatchWinner() {
        if (!finished) {
            throw new IllegalStateException("La partida todavía no terminó.");
        }
        return winner;
    }

    /**
     * Returns the current life-based result. This preserves the controller's
     * ability to query the result without introducing a new exception.
     */
    public Result getMatchResult() {
        if (winner == player1) {
            return Result.PLAYER1_WIN;
        }
        if (winner == player2) {
            return Result.PLAYER2_WIN;
        }

        if (player1.getLives() > player2.getLives()) {
            return Result.PLAYER1_WIN;
        }
        if (player2.getLives() > player1.getLives()) {
            return Result.PLAYER2_WIN;
        }
        return Result.DRAW;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean isFinished() {
        return finished;
    }
}
