package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;


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

    public boolean contains(Card card) {
        return discard.contains(card);
    }

    public boolean isEmpty() {
        return discard.isEmpty();
    }

    public int size() {
        return discard.size();
    }

    public Card last() {
        return discard.isEmpty() ? null : discard.get(discard.size() - 1);
    }

    /**
     * Returns a copy of the current discard pile.
     * @return An ArrayList containing the cards in the discard pile.
    **/
    public ArrayList<Card> getDiscard() {
        return new ArrayList<>(discard);
    }

    public List<Card> view() {
        return List.copyOf(discard);
    }
}