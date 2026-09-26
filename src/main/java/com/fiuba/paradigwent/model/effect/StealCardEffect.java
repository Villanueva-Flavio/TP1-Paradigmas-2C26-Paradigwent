package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;

public class StealCardEffect implements Effect {
    private final int amount;
    
    public StealCardEffect(int amount){
        this.amount = amount;
    }

    @Override
    public void apply(GameContext context){
       context.getCurrentPlayer().drawCard(amount);
    }
}
