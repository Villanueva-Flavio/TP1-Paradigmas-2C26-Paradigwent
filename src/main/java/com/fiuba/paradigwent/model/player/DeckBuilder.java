package com.fiuba.paradigwent.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.CardPools;
import com.fiuba.paradigwent.model.enums.Faction;

/**
 * Builds the deck of a faction following the rules of the game:
 * 3 weather cards, 2 spells, 2 minions with abilities and 23 common minions.
 * When a faction does not have enough different minions, copies are added.
 * Weather and spells are generic, so when a faction lacks them they are
 * completed with the ones of other factions.
 */
public class DeckBuilder {
    public static final int WEATHER_CARDS = 3;
    public static final int SPELL_CARDS = 2;
    public static final int ABILITY_MINIONS = 2;
    public static final int COMMON_MINIONS = 23;
    public static final int DECK_SIZE = WEATHER_CARDS + SPELL_CARDS + ABILITY_MINIONS + COMMON_MINIONS;

    private final List<Card> catalog;
    private final Random random;

    /**
     * @param catalog every card available in the game
     * @param random  source of randomness used to choose cards
     */
    public DeckBuilder(List<Card> catalog, Random random) {
        this.catalog = List.copyOf(catalog);
        this.random = random;
    }

    /**
     * Builds a new deck for the faction. Every card of the deck is a new copy,
     * so the catalog is never modified by a match.
     *
     * @param faction faction of the deck
     * @return the cards of the deck
     */
    public Deck buildFor(Faction faction) {
        CardPools own = new CardPools(catalog.stream().filter(card -> card.belongsTo(faction)).toList());
        CardPools all = new CardPools(catalog);

        List<Card> cards = new ArrayList<>(DECK_SIZE);
        cards.addAll(pick(withFallback(own.weathers(), all.weathers()), WEATHER_CARDS));
        cards.addAll(pick(withFallback(own.spells(), all.spells()), SPELL_CARDS));
        cards.addAll(pick(shuffled(own.abilityMinions()), ABILITY_MINIONS));
        cards.addAll(pick(shuffled(own.commonMinions()), COMMON_MINIONS));
        return new Deck(cards);
    }

    private List<Card> withFallback(List<Card> preferred, List<Card> fallback) {
        List<Card> result = shuffled(preferred);
        shuffled(fallback).stream()
            .filter(card -> !preferred.contains(card))
            .forEach(result::add);
        return result;
    }

    /**
     * Takes the first {@code amount} candidates in order, cycling through
     * them (with new copies) when there are not enough different cards.
     */
    private List<Card> pick(List<Card> ordered, int amount) {
        if (ordered.isEmpty()) {
            throw new IllegalStateException("The catalog does not have cards to complete the deck");
        }
        List<Card> picked = new ArrayList<>(amount);
        for (int index = 0; index < amount; index++) {
            picked.add(ordered.get(index % ordered.size()).copy());
        }
        return picked;
    }

    private List<Card> shuffled(List<Card> cards) {
        List<Card> result = new ArrayList<>(cards);
        Collections.shuffle(result, random);
        return result;
    }
}
