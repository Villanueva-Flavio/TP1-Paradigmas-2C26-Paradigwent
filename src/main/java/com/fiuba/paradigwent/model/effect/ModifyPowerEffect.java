package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.targeting.MinionTarget;
import com.fiuba.paradigwent.model.targeting.TargetSide;
import com.fiuba.paradigwent.model.targeting.Targeting;

/**
 * Adds a modifier to the power of the chosen minion.
 */
public class ModifyPowerEffect implements Effect {
    private final int amount;

    /**
     * @param amount modifier added to the chosen minion (negative values weaken it)
     */
    public ModifyPowerEffect(int amount) {
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(GameContext context) {
        context.chosenTarget().ifPresent(target -> context.modifyPower(target, amount));
    }

    /**
     * Buffs are meant for allies and debuffs for enemies; the target is optional.
     */
    @Override
    public Targeting targeting() {
        String prompt = amount >= 0 ? "Select a minion to buff" : "Select a minion to weaken";
        return new MinionTarget(prompt, true, amount >= 0 ? TargetSide.ALLY : TargetSide.ENEMY);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Modify the power of a minion";
    }

    /** {@inheritDoc} */
    @Override
    public String getTargetDescription() {
        return "Objective: A minion from any line";
    }
}
