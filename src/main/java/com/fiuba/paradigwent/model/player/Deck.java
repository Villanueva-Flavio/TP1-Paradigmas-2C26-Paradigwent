package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.card.Card;

import java.util.*;


public class Deck {

    private final Deque<Card> deck;

    /**
     * Constructs a Deck with the specified list of cards. The provided list is used to initialize the deck, and the cards are shuffled to ensure randomness.
     * @param cards
    **/
    public Deck(List<Card> cards) {
        this.deck = new ArrayDeque<>(cards);
        shuffle();
    }

    /**
     * Draws a card from the top of the deck. If the deck is empty, this method will throw a NoSuchElementException.
     * @return The card drawn from the top of the deck.
     */
    public Card draw() {
        return deck.removeFirst();
    }

    /**
     * Checks if the deck is empty.
     * @return true if the deck is empty, false otherwise.
    **/
    public boolean isEmpty() { 
        return deck.isEmpty(); 
    }
    
    /**
     * Returns the current size of the deck.
     * @return The number of cards remaining in the deck.
    **/
    public int size() { 
        return deck.size(); 
    }

    /**
     * Shuffles the cards in the deck to ensure randomness. This method uses the Collections.shuffle() method to randomize the order of the cards.
    **/
    public void shuffle() {
        List<Card> temp = new ArrayList<>(deck);
        Collections.shuffle(temp);
        this.deck.clear();
        this.deck.addAll(temp);
    }
}
