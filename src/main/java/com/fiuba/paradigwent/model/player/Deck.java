package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class Deck {

    private final Deque<Card> deck;


    public Deck(List<Card> cards) {
        this.deck = new ArrayDeque<>(cards);

    }
    public Card draw() {
        return deck.pop();
    }

    public boolean isEmpty() { return deck.isEmpty(); }
    public int size()        { return deck.size(); }

}
