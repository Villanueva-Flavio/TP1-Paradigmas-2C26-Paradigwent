package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayDeque;
import java.util.List;

public class Player {

    private final String name;
    private int lives = 2;
    private final Deck deck;
    private final Hand hand;
    private final Discard discard;
    private boolean inRound;

    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        draw(10);
        this.discard = new Discard();
        this.inRound = true;
    }

    public void draw(int n) {
        for (int i = 0; i < n && !deck.isEmpty(); i++) hand.add(deck.draw());
    }

    public void play(GameContext context) {
        this.hand.remove(context.getPlayedCard());
        context.getPlayedCard().play(context);

    }

    public void sendToDiscard(List<? extends Card> cards) {
        for (Card card : cards) {
            this.discard.add(card);
        }
    }


    public void playRound() {
        this.inRound = true;
    }

    public void skipTurn() {
        this.inRound = false;
    }


}
