package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;

public class ModifyPowerEffect implements Effect {
    private final int amount;


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
