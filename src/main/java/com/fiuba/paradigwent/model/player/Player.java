package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {
    protected final String name;
    protected boolean hasPassedRound;
    protected int lives = 2;
    protected final Deck deck;
    protected final Hand hand;
    protected final Discard discard;

    /**
     * Constructs a Player with the specified name and deck. Initializes the player's hand, draws 10 cards from the deck, and initializes the discard pile. The player starts with 2 lives and has not passed the round.
     * @param name
     * @param deck
    **/
    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        drawCard(10);
        this.discard = new Discard();
        this.hasPassedRound = false;
    }

    /**
     * Draws n cards from the player's deck and adds them to the player's hand. If the deck has fewer than n cards, it draws as many as possible.
     * @param n
    **/
    public void drawCard(int n) {
        for (int i = 0; i < n && !deck.isEmpty(); i++) hand.add(deck.draw());
    }

    /**
     * Plays the card currently selected in the game context. If the player has passed the round, an IllegalStateException is thrown. The played card is removed from the player's hand and its play method is invoked with the provided game context.
     * @param context
    **/
    public void play(Card card, GameContext context) {
        if (hasPassedRound) {
            throw new IllegalStateException(
                    "A player who passed cannot play during this round");
        }
        this.hand.remove(card);
        card.play(context);
    }


    public void play(GameContext context) {
        play(context.getPlayedCard(), context);
    }

    /**
     * Sends the specified list of cards to the player's discard pile. Each card in the provided list is added to the discard pile.
     * @param cards
    **/
    public void sendToDiscard(List<? extends Card> cards) {
        for (Card card : cards) {
            this.discard.add(card);
        }
    }

    /**
     * @return The list of cards in the player's hand.
    **/
    public ArrayList<Card> getHand() {
        return this.hand.getHand();
    }

    /**
     * @return The list of cards in the player's discard pile.
    **/
    public ArrayList<Card> getDiscard() {
        return this.discard.getDiscard();
    }

    /**
     * @return True if the player has passed the round, false otherwise.
    **/
    public boolean hasPassedRound() {
        return hasPassedRound;
    }

    /**
     * @return True if the player can play during this round, false otherwise.
    **/
    public boolean canPlayThisRound() {
        return !hasPassedRound;
    }

    /**
     * Starts a new round for the player, resetting the hasPassedRound flag to false. This allows the player to play cards during the new round.
    **/
    public void startRound() {
        this.hasPassedRound = false;
    }

    /**
     * Marks the player as having passed the current round, preventing them from playing any more cards during this round.
    **/
    public void passRound() {
        this.hasPassedRound = true;
    }

    /**
     * Decreases the player's lives by one. This method is called when the player loses a life during the game.
    **/
    public void loseLife() {
        this.lives--;
    }

    /**
     * @return The number of lives the player currently has.
    **/
    public int getLives() {
        return this.lives;
    }

    /**
     * @return The name of the player.
    **/
    public String getName() {
        return this.name;
    }

    /**
     * Revives the specified card from the player's discard pile and adds it back to the player's hand. If the card is not in the discard pile, no action is taken.
     * @param card
    **/
    public void reviveCard(Card card) {
        if (discard.getDiscard().contains(card)) {
            discard.remove(card);
            hand.add(card);
        }
    }
}
