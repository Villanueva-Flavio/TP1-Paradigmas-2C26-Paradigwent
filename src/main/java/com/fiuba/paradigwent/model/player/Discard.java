package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;


public class Discard {

    private ArrayList<Card> discard;

    public Discard() {

        this.discard= new ArrayList<>();
    }


    public void add(Card card) {
        this.discard.add(card);
    }

    public void remove(Card card) {
        this.discard.remove(card);
    }

    public ArrayList<Card> getDiscard() {
        ArrayList<Card> copy = (ArrayList<Card>) List.copyOf(discard);
        return copy;
    }
}