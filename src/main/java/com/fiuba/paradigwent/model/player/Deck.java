package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.*;


public class Deck {

    private final Deque<Card> deck;


    public Deck(List<Card> cards) {
        this.deck = new ArrayDeque<>(cards);
        shuffle();

    }
    public Card draw() {
        return deck.removeFirst();
    }

    public boolean isEmpty() { return deck.isEmpty(); }
    public int size()        { return deck.size(); }


    public void shuffle() {
        List<Card> temp = new ArrayList<>(deck);
        Collections.shuffle(temp);
        this.deck.clear();
        this.deck.addAll(temp);
    }

}
