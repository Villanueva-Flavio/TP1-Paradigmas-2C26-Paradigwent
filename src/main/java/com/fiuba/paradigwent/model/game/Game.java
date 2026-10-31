package com.fiuba.paradigwent.model.game;

import java.util.Random;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.event.GameListener;
import com.fiuba.paradigwent.model.event.GameListeners;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;

/**
 * A match between two players. It owns the cycle of rounds and turns:
 * it draws who opens every round, validates every play, resolves finished
 * rounds and asks the player whose turn it is to take it. The match therefore
 * advances by itself; the user interface only observes it.
 */
public class Game implements TurnActions {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private final Random random;
    private final TurnScheduler scheduler;
    private final GameListeners listeners = new GameListeners();
    private Round round;
    private boolean finished;
    private Player winner;
    private int turnNumber;

    /**
     * Creates a match whose turns are taken immediately.
     *
     * @param player1 first player
     * @param player2 second player
     * @param board   board of the match
     */
    public Game(Player player1, Player player2, Board board) {
        this(player1, player2, board, new Random(), new ImmediateTurnScheduler());
    }

    /**
     * Creates a match.
     *
     * @param player1   first player
     * @param player2   second player
     * @param board     board of the match
     * @param random    source used to draw who opens every round
     * @param scheduler decides when every turn is taken
     */
    public Game(Player player1, Player player2, Board board, Random random, TurnScheduler scheduler) {
        if (player1 == null || player2 == null || board == null) {
            throw new IllegalArgumentException("Players and board cannot be null.");
        }
        if (player1 == player2) {
            throw new IllegalArgumentException("Players must be different.");
        }

        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
        this.random = random;
        this.scheduler = scheduler;
        this.finished = false;
        this.winner = null;
    }

    /**
     * Registers an observer of the match.
     *
     * @param listener observer to notify
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Starts the match with its first round.
     */
    public void startMatch() {
        finished = false;
        winner = null;
        startRound();
    }

    private void startRound() {
        Player startingPlayer = random.nextBoolean() ? player1 : player2;
        round = new Round(player1, player2, board, startingPlayer, listeners);
        listeners.roundStarted(startingPlayer);
        scheduleTurn();
    }

    /**
     * Plays a card if it is the player's turn, the card is in its hand and
     * the chosen target satisfies the card.
     */
    @Override
    public boolean play(Player player, Card card, Minion target, AttackLineType line) {
        if (!isTurnOf(player) || card == null || !player.hasCard(card)
                || !card.targeting().isSatisfiedBy(target, line, board.allMinions())) {
            return false;
        }
        listeners.cardPlayed(player, card);
        round.playCard(card, target, line);
        afterAction();
        return true;
    }

    /**
     * Passes the round if it is the player's turn.
     */
    @Override
    public boolean pass(Player player) {
        if (!isTurnOf(player)) {
            return false;
        }
        round.passTurn();
        listeners.turnPassed(player);
        afterAction();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public GameContext contextFor(Player player) {
        return new GameContext(player, opponentOf(player), board, listeners, null, null, null);
    }

    /**
     * @param player a player of the match
     * @return {@code true} if the match is running and it is that player's turn
     */
    public boolean isTurnOf(Player player) {
        return !finished && round != null && round.isTurnOf(player);
    }

    /**
     * @return the player whose turn it is, or {@code null} before the match starts
     */
    public Player getCurrentPlayer() {
        return round == null ? null : round.getCurrentPlayer();
    }

    private void afterAction() {
        if (round.isFinished()) {
            resolveRound();
            return;
        }
        scheduleTurn();
    }

    /**
     * Asks the current player to take its turn. The turn number discards
     * stale requests (for example after a surrender).
     */
    private void scheduleTurn() {
        int scheduledTurn = ++turnNumber;
        Player player = round.getCurrentPlayer();
        scheduler.schedule(() -> {
            if (scheduledTurn == turnNumber && isTurnOf(player)) {
                player.takeTurn(this);
            }
        });
    }

    private void resolveRound() {
        Result result = round.getRoundResult();
        applyRoundResult(result);
        board.discard();
        listeners.roundFinished(result);
        updateMatchState();
        if (finished) {
            listeners.matchFinished(getMatchResult());
        } else {
            startRound();
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

    private void updateMatchState() {
        finished = !player1.isAlive() || !player2.isAlive();

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

    /**
     * Ends the match giving the victory to the other player.
     *
     * @param player player that surrenders
     */
    public void surrender(Player player) {
        if (finished) {
            return;
        }
        if (player != player1 && player != player2) {
            throw new IllegalArgumentException("The player does not belong to this match.");
        }

        winner = opponentOf(player);
        while (player.isAlive()) {
            player.loseLife();
        }
        finished = true;
        turnNumber++;
        listeners.matchFinished(getMatchResult());
    }

    private Player opponentOf(Player player) {
        return player == player1 ? player2 : player1;
    }

    /**
     * @return the result of the match according to the winner and the lives
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

    /**
     * @return the board of the match
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return {@code true} once the match is over
     */
    public boolean isFinished() {
        return finished;
    }
}
