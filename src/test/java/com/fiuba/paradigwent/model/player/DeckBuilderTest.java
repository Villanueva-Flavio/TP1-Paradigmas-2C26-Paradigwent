package com.fiuba.paradigwent.model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.CardPools;
import com.fiuba.paradigwent.model.card.JSONCardLoader;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Faction;

/**
 * Every faction gets a 30 card deck: 3 weather cards, 2 spells,
 * 2 minions with abilities and 23 common minions.
 */
class DeckBuilderTest {

    private final List<Card> catalog = JSONCardLoader.loadCards();

    @Test
    void everyFactionGetsAThirtyCardDeckWithTheRequiredComposition() {
        DeckBuilder builder = new DeckBuilder(catalog, new Random(7));

        for (Faction faction : Faction.values()) {
            List<Card> cards = drawAll(builder.buildFor(faction));
            CardPools pools = new CardPools(cards);

            assertEquals(DeckBuilder.DECK_SIZE, cards.size(), faction.name());
            assertEquals(DeckBuilder.WEATHER_CARDS, pools.weathers().size(), faction.name());
            assertEquals(DeckBuilder.SPELL_CARDS, pools.spells().size(), faction.name());
            assertEquals(DeckBuilder.ABILITY_MINIONS, pools.abilityMinions().size(), faction.name());
            assertEquals(DeckBuilder.COMMON_MINIONS, pools.commonMinions().size(), faction.name());
        }
    }

    @Test
    void minionsBelongToTheFaction() {
        DeckBuilder builder = new DeckBuilder(catalog, new Random(11));

        for (Faction faction : Faction.values()) {
            CardPools pools = new CardPools(drawAll(builder.buildFor(faction)));
            List<Card> factionCards = new ArrayList<>(pools.abilityMinions());
            factionCards.addAll(pools.commonMinions());

            assertTrue(factionCards.stream().allMatch(card -> card.belongsTo(faction)), faction.name());
        }
    }

    @Test
    void everyCardOfTheDeckIsAnIndependentCopy() {
        List<Card> cards = drawAll(new DeckBuilder(catalog, new Random(3)).buildFor(Faction.MURGOL));
        Set<Card> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        distinct.addAll(cards);
        Set<Card> catalogCards = Collections.newSetFromMap(new IdentityHashMap<>());
        catalogCards.addAll(catalog);

        assertEquals(cards.size(), distinct.size());
        assertTrue(cards.stream().noneMatch(catalogCards::contains));
    }

    @Test
    void everyMinionOfTheCatalogHasAPlayableLine() {
        CardPools pools = new CardPools(catalog);
        List<Card> minions = new ArrayList<>(pools.abilityMinions());
        minions.addAll(pools.commonMinions());

        assertTrue(minions.stream().allMatch(card -> card.getAttackLineType() != AttackLineType.NONE));
    }

    private List<Card> drawAll(Deck deck) {
        List<Card> cards = new ArrayList<>();
        while (!deck.isEmpty()) {
            cards.add(deck.draw());
        }
        return cards;
    }
}
