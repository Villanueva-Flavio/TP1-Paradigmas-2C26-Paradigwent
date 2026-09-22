package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.Card;
import com.fiuba.paradigwent.model.GameContext;

public class BuffEffect implements Effect {
    private final int amount;
    
    public BuffEffect(int amount) {
        this.amount = amount;
    }

    @Override 
    public void apply(GameContext context){
        Card target = context.getTargetCard();
        if(target != null){
            target.buff(amount);
        } else if (context.getPlayedCard() != null){
            context.getPlayedCard().buff(amount);
        }
    }
}
