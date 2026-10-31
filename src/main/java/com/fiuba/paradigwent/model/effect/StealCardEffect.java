package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;

/**
 * Moves cards from the opponent's hand to the player's hand.
 */
public class StealCardEffect implements Effect {
    private final int amount;

    /**
     * @param amount maximum number of cards to steal
     */
    public StealCardEffect(int amount) {
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(GameContext context) {
        context.stealCards(amount);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Steals " + (amount == 1 ? "a card" : amount + " cards");
    }
}
