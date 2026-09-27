package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.GameContext;

public class DuplicateLineEffect implements Effect {
    private final AttackLineType lineType;

    /**
     * Constructs a DuplicateLineEffect with the specified line type. If the provided lineType is null or NONE, the effect will target the attack line of the played card.
     * @param lineType
    **/
    public DuplicateLineEffect(AttackLineType lineType){
        this.lineType = lineType;
    }

    @Override
    public void apply(GameContext context){
        AttackLineType targetLine = (lineType != null && lineType != AttackLineType.NONE)?
            lineType : context.getPlayedCard().getAttackLineType();
        
        context.getBoard().getPlayerSide().getAttackLine(targetLine).activateHorn();
    }
}
