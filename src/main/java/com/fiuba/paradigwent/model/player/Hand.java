package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private ArrayList<Card> hand;

    /**
     * Constructs an empty hand.
     */
    public Hand() {
        this.hand = new ArrayList<>();
    }

    /**
     * Updates the collection through add.
     * @param card the value used by the method
     */
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
    /**
     * Executes the operation represented by contains.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public boolean contains(Card card) {
        return hand.contains(card);
    }
    /**
     * Executes the operation represented by size.
     * @return the value produced by the method
     */
    public int size() {
        return hand.size();
    }
    /**
     * Reports the state represented by isEmpty.
     * @return the value produced by the method
     */
    public boolean isEmpty() {
        return hand.isEmpty();
    }
    /**
     * Updates the collection through removeLast.
     * @return the value produced by the method
     */
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
    /**
     * Executes the operation represented by view.
     * @return the value produced by the method
     */
    public List<Card> view() {
        return List.copyOf(hand);
    }
}
