package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private ArrayList<Card> hand;
    
    /**
     * Constructs a new Hand with an empty list of cards.
    **/
    public Hand() {
        this.hand = new ArrayList<>();
    }

    /**
     * Adds a card to the hand.
     * @param card
    **/
    public void add(Card card) {
        this.hand.add(card);
    }

    /**
     * Removes a card from the hand.
     * @param c
    **/
    public void remove(Card c) {
        this.hand.remove(c);
    }

    public boolean contains(Card card) {
        return hand.contains(card);
    }

    public int size() {
        return hand.size();
    }

    public boolean isEmpty() {
        return hand.isEmpty();
    }

    public Card removeLast() {
        return hand.isEmpty() ? null : hand.remove(hand.size() - 1);
    }

    /**
     * Returns a copy of the current hand.
     * @return An ArrayList containing the cards in the hand.
    **/
    public ArrayList<Card> getHand() {
        return new ArrayList<>(hand);
    }

    public List<Card> view() {
        return List.copyOf(hand);
    }
}