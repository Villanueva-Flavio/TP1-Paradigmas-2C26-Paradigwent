package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;

public class Player {

    private final String name;
    private int lives = 2;
    private final Deck deck;
    private final Hand hand;


    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        draw(10);
    }

    public void draw(int n) {
        for (int i = 0; i < n && !deck.isEmpty(); i++) hand.add(deck.draw());
    }

    public void play(GameContext context) {
        hand.remove(context.getPlayedCard());
        context.getPlayedCard().play(context);

    }


}
