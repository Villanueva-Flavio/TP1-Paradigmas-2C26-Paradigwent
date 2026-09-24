package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.GameContext;

public class DuplicateLineEffect implements Effect {
    private final AttackLineType lineType;

    public DuplicateLineEffect(AttackLineType lineType){
        this.lineType = lineType;
    }

    @Override
    public void apply(GameContext context){
        AttackLineType targetLine = (lineType != null && lineType != AttackLineType.NONE)?
            lineType : context.getPlayedCard().getAttackLine(); // REVISAR TDA BROKEN
        
        context.getBoard().activateHorn(context.getCurrentPlayer(), targetLine);
    }
}
