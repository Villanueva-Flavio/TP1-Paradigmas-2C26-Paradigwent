package com.fiuba.paradigwent.model;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public class GameContext {
    private final Player currentPlayer;
    private final Player opponent;
    private final Board board;
    private final Card playedCard;
    private final Card targetCard;
    private final AttackLineType targetLine;

    /**
     * Constructs a GameContext with the specified current player, opponent, board, and played card.
     * @param currentPlayer
     * @param opponent
     * @param board
     * @param playedCard
    **/
    public GameContext(Player currentPlayer, Player opponent, Board board, Card playedCard) {
        this(currentPlayer, opponent, board, playedCard, null);
    }

    public GameContext(Player currentPlayer, Player opponent, Board board,
                       Card playedCard, Card targetCard) {
        this(currentPlayer, opponent, board, playedCard, targetCard, null);
    }

    public GameContext(Player currentPlayer, Player opponent, Board board,
                       Card playedCard, Card targetCard, AttackLineType targetLine) {
        this.currentPlayer = currentPlayer;
        this.opponent = opponent;
        this.board = board;
        this.playedCard = playedCard;
        this.targetCard = targetCard;
        this.targetLine = targetLine;
    }

    /**
     * @return The current player in the game context.
    **/
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return The opponent player in the game context.
    **/
    public Player getOpponent() {
        return opponent;
    }

    /**
     * @return The board in the game context.
    **/
    public Board getBoard() {
        return board;
    }

    /**
     * @return The card that was played in the game context.
    **/
    public Card getPlayedCard() {
        return playedCard;
    }

    /**
     * @return The target card on the board in the game context.
    **/
    public Card getTargetCard() {
        return targetCard;
    }

    public AttackLineType getTargetLine() {
        return targetLine;
    }
}
