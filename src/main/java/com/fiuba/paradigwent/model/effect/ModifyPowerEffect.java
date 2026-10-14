package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;

public class ModifyPowerEffect implements Effect {
    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Modify the power of a minion";
    }

    @Override
    public String getTargetDescription() {
        return "Objective: A minion from any line";
    }
    private final int amount;

    /**
     * Constructs a ModifyPowerEffect with the specified amount to modify the power of a target minion.
     * @param amount
    **/
    public ModifyPowerEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(GameContext context){
       if (context.getTargetCard() instanceof Minion target) {
            target.modifyPower(amount);
        }
    }
}
