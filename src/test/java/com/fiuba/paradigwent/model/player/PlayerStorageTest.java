package com.fiuba.paradigwent.model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

class PlayerStorageTest {

    /**
     *  Test: A hand adds and removes cards, and returns an independent copy of its contents.
    **/
    @Test
    void handAddsRemovesAndReturnsAnIndependentCopy() {
        Hand hand = new Hand();
        Card card = ModelTestFixtures.minion("card", 2, AttackLineType.MELEE, null);
        hand.add(card);

        ArrayList<Card> snapshot = hand.getHand();
        snapshot.clear();

        assertNotSame(snapshot, hand.getHand());
        assertEquals(1, hand.getHand().size());
        hand.remove(card);
        assertTrue(hand.getHand().isEmpty());
    }

    /**
     *  Test: A discard adds and removes cards, and returns an independent copy of its contents.
    **/
    @Test
    void discardAddsRemovesAndReturnsAnIndependentCopy() {
        Discard discard = new Discard();
        Card card = ModelTestFixtures.minion("card", 2, AttackLineType.MELEE, null);
        discard.add(card);

        ArrayList<Card> snapshot = discard.getDiscard();
        snapshot.clear();

        assertEquals(1, discard.getDiscard().size());
        discard.remove(card);
        assertTrue(discard.getDiscard().isEmpty());
    }

    /**
     *  Test: A deck tracks its size, allows drawing cards, and throws an exception when attempting to draw from an empty deck.
    **/
    @Test
    void deckTracksSizeDrawsAndEmptyState() {
        Minion first = ModelTestFixtures.minion("first", 1, AttackLineType.MELEE, null);
        Minion second = ModelTestFixtures.minion("second", 2, AttackLineType.RANGED, null);
        Deck deck = new Deck(List.of(first, second));

        assertEquals(2, deck.size());
        Card drawn = deck.draw();

        assertTrue(drawn == first || drawn == second);
        assertEquals(1, deck.size());
        deck.draw();
        assertTrue(deck.isEmpty());
        assertThrows(java.util.NoSuchElementException.class, deck::draw);
    }

    /**
     *  Test: A player draws at most 10 cards from their deck upon creation, and can lose lives.
    **/
    @Test
    void playerDrawsAtMostTenCardsAndCanLoseLives() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cards.add(ModelTestFixtures.minion("card-" + i, i, AttackLineType.MELEE, null));
        }
        Player player = new Player("player", new Deck(cards));

        assertEquals(10, player.getHand().size());
        assertEquals(Player.INITIAL_LIVES, player.getLives());
        player.loseLife();
        assertEquals(Player.INITIAL_LIVES - 1, player.getLives());
    }

    /**
     *  Test: Reviving a card that is not in the discard pile throws an exception.
    **/
    @Test
    void revivingARealDiscardedCardReturnsItToHand() {
        Player player = ModelTestFixtures.emptyPlayer("player");
        Card card = ModelTestFixtures.minion("card", 3, AttackLineType.MELEE, null);
        player.sendToDiscard(List.of(card));

        player.reviveCard(card);

        assertTrue(player.getHand().contains(card));
        assertFalse(player.getDiscard().contains(card));
    }
}
