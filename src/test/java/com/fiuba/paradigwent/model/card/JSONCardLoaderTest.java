package com.fiuba.paradigwent.model.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.enums.AttackLineType;

class JSONCardLoaderTest {

    @Test
    void loadsCardsFromClasspathResource() {
        List<Card> cards = JSONCardLoader.loadCards();

        assertNotNull(cards);
        assertFalse(cards.isEmpty());
        assertEquals(100, cards.size());
    }

    @Test
    void deserializesEverySupportedCardType() {
        List<Card> cards = JSONCardLoader.loadCards();

        assertTrue(cards.stream().anyMatch(Minion.class::isInstance));
        assertTrue(cards.stream().anyMatch(Spell.class::isInstance));
        assertTrue(cards.stream().anyMatch(Weather.class::isInstance));
    }

    @Test
    void deserializesMinionFieldsAndSubtype() {
        List<Card> cards = JSONCardLoader.loadCards();

        Minion firstCard = assertInstanceOf(Minion.class, cards.get(0));

        assertEquals(AttackLineType.RANGED, firstCard.getAttackLineType());
        assertEquals(4, firstCard.getCurrentPower());
    }
}
