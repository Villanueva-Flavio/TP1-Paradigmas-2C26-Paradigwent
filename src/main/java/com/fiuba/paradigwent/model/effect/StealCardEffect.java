package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;

public class StealCardEffect implements Effect {
    private final int amount;
    
    /**
     * Constructs a StealCardEffect with the specified amount of cards to steal from the opponent's hand.
     * @param amount
    **/
    public StealCardEffect(int amount){
        this.amount = amount;
    }

    @Override 
    public String getDescription() {
        return "Steals " + (amount == 1 ? "a card" : amount + " cards");
    }

    @Override
    public void apply(GameContext context){
       context.getCurrentPlayer().stealCardsFrom(context.getOpponent(), amount);
    }
}
