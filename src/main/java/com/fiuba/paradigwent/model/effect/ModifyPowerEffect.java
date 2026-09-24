package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;

public class ModifyPoweEffect implements Effect {
    private final int amount;

    public ModifyPoweEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(GameContext context){
       Minion target = context.getTargetCard();
        if(target != null){
            target.modifyPower(amount);
        } else if (context.getPlayedCard() != null){
            context.getPlayedCard().modifyPower(amount);
        }
    }
}
