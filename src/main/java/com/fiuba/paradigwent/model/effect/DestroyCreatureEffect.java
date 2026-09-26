package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.Collections;

/* incompleto targetType */
public class DestroyCreatureEffect implements Effect{
    private final AttackLineType targetType;

    public DestroyCreatureEffect(AttackLineType targetType){
        this.targetType = targetType;
    }

    @Override 
    public void apply(GameContext context){
        Minion target = (Minion) context.getTargetCard();
        PlayerSide opponent = context.getBoard().getOpponentSide();

        if(target == null){
            target = context.getBoard().getStrongestMiniondOf(opponent);
        } else {
            opponent.getAttackLine(targetType).removeMinion(target);
            opponent.getOwner().sendToDiscard(Collections.singletonList(target));
        }
    }
}
