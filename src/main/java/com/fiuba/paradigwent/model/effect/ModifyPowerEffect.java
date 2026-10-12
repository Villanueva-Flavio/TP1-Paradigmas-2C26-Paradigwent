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
        return "Modificar poder de un minion";
    }

    @Override
    public String getTargetDescription() {
        return "Objetivo: un minion de cualquier línea";
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
       Minion target = (Minion) context.getTargetCard();
        if(target != null){
            target.modifyPower(amount);
        }
    }
}
