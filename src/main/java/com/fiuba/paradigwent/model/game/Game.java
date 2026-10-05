package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.AI.AI;
import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;

public class Game {
    private GameContext gameContext;
    private boolean finished;
    Player player;
    Player opponent;
    Board board;

    public Game() {
        this.finished = false;
    }

    public void startMatch()
    {   Deck deck = new Deck( );
        player = new Player();
        opponent = selectOpponent();
        board = new Board(player, opponent);
    }

    public void playMatch(){
        while (!finished) {

        }
    }

}
