package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.GameContext;

public class DuplicateLineEffect implements Effect {
    private final AttackLineType lineType;

    public DuplicateLineEffect(AttackLineType lineType){
        this.lineType = lineType;
    }

    @Override
    public boolean requiresTarget() {
        return lineType == null || lineType == AttackLineType.NONE;
    }

    @Override
    public String getDescription() {
        return "Duplicates attack line damage";
    }

    @Override
    public String getTargetDescription() {
        return lineType == null || lineType == AttackLineType.NONE
            ? "Objective: The attack line of the played card"
            : "Objective: The " + lineType + " attack line";
    }

    @Override
    public void apply(GameContext context){
        AttackLineType targetLine = (lineType != null && lineType != AttackLineType.NONE)
            ? lineType : context.getTargetLine();
        if (targetLine == null || targetLine == AttackLineType.NONE) {
            targetLine = context.getPlayedCard().getAttackLineType();
        }
        
        context.getBoard().getSideFor(context.getCurrentPlayer())
            .getAttackLine(targetLine).activateHorn();
    }
}
