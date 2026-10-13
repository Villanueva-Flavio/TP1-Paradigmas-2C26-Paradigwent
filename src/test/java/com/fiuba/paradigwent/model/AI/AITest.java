package com.fiuba.paradigwent.model.AI;

import com.fiuba.paradigwent.model.AI.AI;
import com.fiuba.paradigwent.model.AI.RandomAIStrategy;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Deck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AI} class.
 * These tests verify that an AI instance is correctly constructed,
 * inheriting the initialization behavior from {@link com.fiuba.paradigwent.model.player.Player}.
 */
class AITest {

    /**
     * Tests the construction of an {@link AI} instance.
     * Verifies that the AI is created with the given name and deck,
     * that it draws the initial 10 cards from the deck into its hand,
     * that it starts with 2 lives, and that it has not passed the round.
     */
    @Test
    void testAIConstruction() {
        List<Minion> cards = createMinionDeck(10, 3, AttackLineType.MELEE);
        Deck deck = new Deck(new ArrayList<>(cards));
        AI ai = new AI("AI Player", deck, new RandomAIStrategy(new Random()));

        assertEquals("AI Player", ai.getName());
        assertEquals(10, ai.getHand().size());
        assertEquals(2, ai.getLives());
        assertFalse(ai.hasPassedRound());
        assertTrue(ai.canPlayThisRound());
    }

    /**
     * Tests that constructing an {@link AI} with an empty deck results in an empty hand.
     * The AI should still be initialized with 2 lives and not having passed the round.
     */
    @Test
    void testAIConstructionWithEmptyDeck() {
        Deck deck = new Deck(new ArrayList<>());
        AI ai = new AI("Empty AI", deck, new RandomAIStrategy(new Random()));

        assertEquals("Empty AI", ai.getName());
        assertEquals(0, ai.getHand().size());
        assertEquals(2, ai.getLives());
        assertFalse(ai.hasPassedRound());
        assertTrue(ai.canPlayThisRound());
    }

    /**
     * Creates a list of {@link Minion} cards for testing purposes.
     *
     * @param amount   number of minions to create
     * @param power    base power of each minion
     * @param lineType attack line type of each minion
     * @return a list of minions with the given characteristics
     */
    private List<Minion> createMinionDeck(int amount, int power, AttackLineType lineType) {
        List<Minion> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            list.add(new Minion("minion-" + i, "Minion " + i, 1, power, lineType, null));
        }
        return list;
    }
}
