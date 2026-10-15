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
        assertTrue(cards.size() > 0);
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

    @Test
    void loadsOptionalArtworkConfigurationWithSafeDefaults() {
        List<Card> cards = JSONCardLoader.loadCards();

        Card configured = cards.stream()
            .filter(card -> "Tito the oracle".equals(card.getName()))
            .findFirst()
            .orElseThrow();
        Card defaultArtwork = cards.stream()
            .filter(card -> "Mountain Crossbowman".equals(card.getName()))
            .findFirst()
            .orElseThrow();
        Card titleConfigured = cards.stream()
            .filter(card -> "Freezing tsunami".equals(card.getName()))
            .findFirst()
            .orElseThrow();

        assertEquals(1.2, configured.getArtworkScale());
        assertEquals(20, configured.getArtworkOffsetY(false));
        assertEquals(10, configured.getArtworkOffsetY(true));
        assertEquals(40, configured.getArtworkTitleOffsetY());
        assertEquals(20, titleConfigured.getArtworkTitleOffsetY());
        assertEquals(1.1, defaultArtwork.getArtworkScale());
        assertEquals(0, defaultArtwork.getArtworkOffsetY(false));
        assertEquals(0, defaultArtwork.getArtworkTitleOffsetY());
    }
}
