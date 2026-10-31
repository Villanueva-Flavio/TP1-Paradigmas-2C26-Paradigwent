package com.fiuba.paradigwent.model.card;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups cards by the role they have inside a deck. Cards add themselves
 * through {@link Card#classifyInto(CardPools)}, so no type checks are needed.
 */
public class CardPools {
    private final List<Card> weathers = new ArrayList<>();
    private final List<Card> spells = new ArrayList<>();
    private final List<Card> abilityMinions = new ArrayList<>();
    private final List<Card> commonMinions = new ArrayList<>();

    /**
     * Classifies every card of the list.
     *
     * @param cards cards to classify
     */
    public CardPools(List<Card> cards) {
        cards.forEach(card -> card.classifyInto(this));
    }

    /** @param weather a weather card */
    public void addWeather(Card weather) {
        weathers.add(weather);
    }

    /** @param spell a spell card */
    public void addSpell(Card spell) {
        spells.add(spell);
    }

    /** @param minion a minion with at least one ability */
    public void addAbilityMinion(Card minion) {
        abilityMinions.add(minion);
    }

    /** @param minion a minion without abilities */
    public void addCommonMinion(Card minion) {
        commonMinions.add(minion);
    }

    /** @return the weather cards */
    public List<Card> weathers() {
        return List.copyOf(weathers);
    }

    /** @return the spell cards */
    public List<Card> spells() {
        return List.copyOf(spells);
    }

    /** @return the minions with abilities */
    public List<Card> abilityMinions() {
        return List.copyOf(abilityMinions);
    }

    /** @return the minions without abilities */
    public List<Card> commonMinions() {
        return List.copyOf(commonMinions);
    }
}
