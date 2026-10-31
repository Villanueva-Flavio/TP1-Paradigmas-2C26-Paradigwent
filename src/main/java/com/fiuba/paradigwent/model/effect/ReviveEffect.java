package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;

/**
 * Brings the most recently discarded minions of the player back to the board.
 */
public class ReviveEffect implements Effect {
    private final int amount;

    /**
     * @param amount maximum number of minions to revive
     */
    public ReviveEffect(int amount) {
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(GameContext context) {
        context.reviveMinions(amount);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Revives " + (amount == 1 ? "a card" : amount + " cards");
    }
}
