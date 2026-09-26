package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private boolean isInRound;
    private int lives = 2;
    private final Deck deck;
    private final Hand hand;
    private final Discard discard;

    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        drawCard(10);
        this.discard = new Discard();
        this.isInRound = true;
    }

    public void drawCard(int n) {
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

    public ArrayList<Card> getHand() {
        return this.hand.getHand();
    }

    public ArrayList<Card> getDiscard() {
        return this.discard.getDiscard();
    }

    public void playRound() {
        this.isInRound = true;
    }

    public void skipTurn() {
        this.isInRound = false;
    }

    public void loseLife() {
        this.lives--;
    }

    public int getLives() {
        return this.lives;
    }

    public String getName() {
        return this.name;
    }

    public void reviveCard(Card card) {
        if (discard.getDiscard().contains(card)) {

            discard.remove(card);
            hand.add(card);
        }
    }
}
