package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;

public class Game {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private boolean finished;

    public Game(Player player1, Player player2, Board board) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
        this.finished = false;
    }

    public void startMatch() {
        this.finished = false;
        player1.startRound();
        player2.startRound();
    }

    public void playMatch() {
        while (!finished) {
            Round round = new Round(player1, player2, board);
            playRound(round);

            Result result = round.getRoundResult();
            applyRoundResult(result);

            board.discard();

            finished = checkMatchFinished();
        }
    }

    private void playRound(Round round) {
        while (!round.isFinished()) {
            Player current = round.getCurrentPlayer();
        }
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

    public Player checkMatchWinner() {
        if (!finished) {
            throw new IllegalStateException("Match is not finished yet.");
        }

        if (player1.getLives() > player2.getLives()) return player1;
        if (player2.getLives() > player1.getLives()) return player2;
        return null;
    }

    private boolean checkMatchFinished() {
        return player1.getLives() <= 0 || player2.getLives() <= 0;
    }

    public Board getBoard() {
        return board;
    }

    public boolean resolveRound(Round round) {
        if (finished || round == null || !round.isFinished()) {
            return false;
        }
        applyRoundResult(round.getRoundResult());
        board.discard();
        finished = checkMatchFinished();
        if (!finished) {
            player1.startRound();
            player2.startRound();
            player1.drawCard(10);
            player2.drawCard(10);
        }
        return true;
    }

    public boolean isFinished() {
        return finished;
    }

    public void surrender(Player player) {
        if (finished) {
            return;
        }
        if (player != player1 && player != player2) {
            throw new IllegalArgumentException("El jugador no pertenece a esta partida");
        }
        while (player.getLives() > 0) {
            player.loseLife();
        }
        finished = true;
    }
}