package com.fiuba.paradigwent.model;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.player.Player;

public class GameContext {
    private final Player currentPlayer;
    private final Player opponent;
    private final Board board;
    private final Card playedCard;

    public GameContext(Player currentPlayer, Player opponent, Board board, Card playedCard) {
        this.currentPlayer = currentPlayer;
        this.opponent = opponent;
        this.board = board;
        this.playedCard = playedCard;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponent() {
        return opponent;
    }

    public Board getBoard() {
        return board;
    }

    public Card getPlayedCard() {
        return playedCard;
    }

    public Card getTargetCard() {
        return board.getTargetCard();
    }
}
