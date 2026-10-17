package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.GameContext;

public class DuplicateLineEffect implements Effect {
    private final AttackLineType lineType;

    /**      * Constructs a new DuplicateLineEffect.      */
    public DuplicateLineEffect(AttackLineType lineType){
        this.lineType = lineType;
    }
    /**
     * Executes the operation represented by requiresTarget.
     * @return the value produced by the method
     */
    @Override
    public boolean requiresTarget() {
        return lineType == null || lineType == AttackLineType.NONE;
    }
    /**
     * Reports the state represented by isLineTargetEffect.
     * @return the value produced by the method
     */
    @Override
    public boolean isLineTargetEffect() {
        return true;
    }
    /**
     * Reports the state represented by isDuplicateLineEffect.
     * @return the value produced by the method
     */
    @Override
    public boolean isDuplicateLineEffect() {
        return true;
    }
    /**
     * Returns the value provided by getDescription.
     * @return the value produced by the method
     */
    @Override
    public String getDescription() {
        return "Duplicates attack line damage";
    }
    /**
     * Returns the value provided by getTargetDescription.
     * @return the value produced by the method
     */
    @Override
    public String getTargetDescription() {
        return lineType == null || lineType == AttackLineType.NONE
            ? "Objective: The attack line of the played card"
            : "Objective: The " + lineType + " attack line";
    }
    /**
     * Executes the operation represented by apply.
     * @param context the value used by the method
     */
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
