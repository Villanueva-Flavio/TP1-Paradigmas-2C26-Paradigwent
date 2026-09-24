package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.player.Player;

/* incompleto targetType */
public class DestroyCreatureEffect implements Effect{
    private final String targetType;

    public DestroyCreatureEffect(String targetType){
        this.targetType = targetType;
    }

    @Override 
    public void apply(GameContext context){
        Minion target = context.getTargetCard();
        Player opponent = context.getOpponent();

        if(target == null){
            target = context.getBoard().getStrongestCardOf(opponent);
        } else {
            context.getBoard().removeCard(target);
            opponent.sendToDiscard(target);
        }
    }
}
