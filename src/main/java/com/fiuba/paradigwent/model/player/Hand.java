package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private ArrayList<Card> hand;

    public Hand() {
        this.hand = new ArrayList<>();
    }

    public void add(Card card) {
        this.hand.add(card);
    }

    public void remove(Card c) {
        this.hand.remove(c);
    }

    public ArrayList<Card> getHand() {
        ArrayList<Card> copy = (ArrayList<Card>) List.copyOf(hand);
        return copy;
    }
}
