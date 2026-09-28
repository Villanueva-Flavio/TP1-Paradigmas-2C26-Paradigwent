package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;


public class Discard {

    private ArrayList<Card> discard;

    /**
     * Constructs a new Discard with an empty list of discarded cards.
    **/
    public Discard() {
        this.discard= new ArrayList<>();
    }

    /**
     * Adds a card to the discard pile.
     * @param card
    **/
    public void add(Card card) {
        this.discard.add(card);
    }

    /**
     * Removes a card from the discard pile.
     * @param card
    **/
    public void remove(Card card) {
        this.discard.remove(card);
    }

    /**
     * Returns a copy of the current discard pile.
     * @return An ArrayList containing the cards in the discard pile.
    **/
    public ArrayList<Card> getDiscard() {
        return new ArrayList<>(discard);
    }
}