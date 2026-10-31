package com.fiuba.paradigwent.model.player;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.event.EffectListener;
import com.fiuba.paradigwent.model.game.TurnActions;

import java.util.ArrayList;
import java.util.List;

/**
 * A participant of the match. It owns its deck, hand and discard pile and
 * keeps track of its lives and of whether it passed the current round.
 * A plain player is controlled by a human through the controller.
 */
public class Player {
    public static final int INITIAL_LIVES = 3;
    public static final int INITIAL_HAND_SIZE = 10;

    protected final String name;
    protected boolean hasPassedRound;
    protected int lives = INITIAL_LIVES;
    protected final Deck deck;
    protected final Hand hand;
    protected final Discard discard;
    private Faction faction;

    /**
     * Creates a player and draws the initial hand.
     *
     * @param name display name
     * @param deck deck of the player
     */
    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        this.discard = new Discard();
        this.hasPassedRound = false;
        drawCard(INITIAL_HAND_SIZE);
    }

    /**
     * Called when it is this player's turn. A human acts through the
     * controller, so nothing happens automatically.
     *
     * @param actions the actions available during the turn
     */
    public void takeTurn(TurnActions actions) {
    }

    /**
     * @param faction faction of the player's deck
     */
    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    /**
     * @return faction of the player's deck
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Draws cards while the deck is not empty.
     *
     * @param n number of cards to draw
     */
    public void drawCard(int n) {
        for (int i = 0; i < n && !deck.isEmpty(); i++) hand.add(deck.draw());
    }

    /**
     * Plays a card of the hand.
     *
     * @param card    card to play
     * @param context operations available for the play
     * @throws IllegalStateException    if the player already passed
     * @throws IllegalArgumentException if the card is not in the hand
     */
    public void play(Card card, GameContext context) {
        if (hasPassedRound) {
            throw new IllegalStateException(
                    "A player who passed cannot play during this round");
        }
        if (card == null || !hand.contains(card)) {
            throw new IllegalArgumentException("The card must belong to the player's hand");
        }
        this.hand.remove(card);
        card.play(context);
    }

    /**
     * Sends cards to the discard pile.
     *
     * @param cards cards to discard
     */
    public void sendToDiscard(List<? extends Card> cards) {
        for (Card card : cards) {
            this.discard.add(card);
        }
    }

    /**
     * @return a copy of the cards in hand
     */
    public ArrayList<Card> getHand() {
        return this.hand.getHand();
    }

    /**
     * @param card a card
     * @return {@code true} if the card is in hand
     */
    public boolean hasCard(Card card) {
        return hand.contains(card);
    }

    /**
     * @return number of cards in hand
     */
    public int handSize() {
        return hand.size();
    }

    /**
     * @return a copy of the discard pile, oldest first
     */
    public ArrayList<Card> getDiscard() {
        return this.discard.getDiscard();
    }

    /**
     * @return the last discarded card, or {@code null}
     */
    public Card lastDiscarded() {
        List<Card> cards = discard.view();
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    /**
     * @return number of cards left in the deck
     */
    public int getDeckSize() {
        return deck.size();
    }

    /**
     * @return {@code true} if the player passed the current round
     */
    public boolean hasPassedRound() {
        return hasPassedRound;
    }

    /**
     * @return {@code true} if the player can still play this round
     */
    public boolean canPlayThisRound() {
        return !hasPassedRound;
    }

    /**
     * Resets the round state of the player.
     */
    public void startRound() {
        this.hasPassedRound = false;
    }

    /**
     * Marks the player as passed for the rest of the round.
     */
    public void passRound() {
        this.hasPassedRound = true;
    }

    /**
     * Removes one life.
     */
    public void loseLife() {
        this.lives--;
    }

    /**
     * @return remaining lives
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * @return {@code true} while the player has lives left
     */
    public boolean isAlive() {
        return lives > 0;
    }

    /**
     * @return display name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Moves a card from the discard pile back to the hand.
     *
     * @param card card to recover
     */
    public void reviveCard(Card card) {
        if (discard.contains(card)) {
            discard.remove(card);
            hand.add(card);
        }
    }

    /**
     * Brings the most recently discarded cards that can be revived back to the board.
     *
     * @param amount   maximum number of cards to revive
     * @param side     side of the board of this player
     * @param listener observer notified of every revived card
     * @return number of revived cards
     */
    public int reviveLatestMinions(int amount, PlayerSide side, EffectListener listener) {
        List<Card> candidates = new ArrayList<>(discard.view());
        int revived = 0;
        for (int index = candidates.size() - 1; index >= 0 && revived < amount; index--) {
            Card card = candidates.get(index);
            if (card.reviveOnto(side, listener)) {
                discard.remove(card);
                revived++;
            }
        }
        return revived;
    }

    /**
     * Takes cards from the end of the opponent's hand.
     *
     * @param opponent player to steal from
     * @param amount   maximum number of cards to steal
     * @return number of stolen cards
     */
    public int stealCardsFrom(Player opponent, int amount) {
        if (opponent == null || amount <= 0) {
            return 0;
        }
        int stolen = 0;
        while (stolen < amount) {
            Card card = opponent.hand.removeLast();
            if (card == null) {
                break;
            }
            hand.add(card);
            stolen++;
        }
        return stolen;
    }
}
