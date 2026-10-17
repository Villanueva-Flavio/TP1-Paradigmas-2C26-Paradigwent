package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;


public class Discard {

    private ArrayList<Card> discard;

    /**
     * Constructs an empty discard pile.
     */
    public Discard() {
        this.discard = new ArrayList<>();
    }

    /**
     * Updates the collection through add.
     * @param card the value used by the method
     */
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
     * Executes the operation represented by contains.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public boolean contains(Card card) {
        return discard.contains(card);
    }
    /**
     * Reports the state represented by isEmpty.
     * @return the value produced by the method
     */
    public boolean isEmpty() {
        return discard.isEmpty();
    }
    /**
     * Executes the operation represented by size.
     * @return the value produced by the method
     */
    public int size() {
        return discard.size();
    }

    /**
     * Returns a copy of the current discard pile.
     * @return An ArrayList containing the cards in the discard pile.
    **/
    public ArrayList<Card> getDiscard() {
        return new ArrayList<>(discard);
    }
    /**
     * Executes the operation represented by view.
     * @return the value produced by the method
     */
    public List<Card> view() {
        return List.copyOf(discard);
    }
}
