package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.Collections;

/* incompleto targetType */
public class DestroyCreatureEffect implements Effect{
    private final boolean scorch;
    /**
     * Executes the operation represented by requiresTarget.
     * @return the value produced by the method
     */
    @Override
    public boolean requiresTarget() {
        return true;
    }
    /**
     * Reports the state represented by isDestroyCreatureEffect.
     * @return the value produced by the method
     */
    @Override
    public boolean isDestroyCreatureEffect() {
        return true;
    }
    /**
     * Returns the value provided by getTargetPrompt.
     * @return the value produced by the method
     */
    @Override
    public String getTargetPrompt() {
        return scorch ? "Select a minion to scorch" : "Select a minion to destroy";
    }
    /**
     * Returns the value provided by getDescription.
     * @return the value produced by the method
     */
    @Override
    public String getDescription() {
        return "Destroys a minion";
    }
    /**
     * Returns the value provided by getTargetDescription.
     * @return the value produced by the method
     */
    @Override
    public String getTargetDescription() {
        return targetType == AttackLineType.NONE
            ? "Objective: A minion"
            : "Objective: A minion from  " + targetType + " line";
    }
    private final AttackLineType targetType;

    /**
     * Constructs a DestroyCreatureEffect with the specified target type. If the provided targetType is null, it defaults to AttackLineType.NONE.
     * @param targetType 
    **/
    public DestroyCreatureEffect(AttackLineType targetType){
        this(targetType, false);
    }

    /**      * Constructs a new DestroyCreatureEffect.      */
    public DestroyCreatureEffect(AttackLineType targetType, boolean scorch){
        this.targetType = targetType;
        this.scorch = scorch;
    }
    /**
     * Reports the state represented by isScorch.
     * @return the value produced by the method
     */
    public boolean isScorch() {
        return scorch;
    }
    /**
     * Executes the operation represented by apply.
     * @param context the value used by the method
     */
    @Override 
    public void apply(GameContext context){
        Minion target = context.getTargetCard() == null
            ? null
            : context.getTargetCard().asMinion().orElse(null);
        if(target == null){
            target = strongestTarget(context);
        }
        if (target != null) {
            var owner = context.getBoard().ownerOf(target);
            if (context.getBoard().removeMinion(target)) {
                owner.sendToDiscard(Collections.singletonList(target));
            }
        }
    }
    /**
     * Executes the operation represented by strongestTarget.
     * @param context the value used by the method
     * @return the value produced by the method
     */
    private Minion strongestTarget(GameContext context) {
        if (targetType == null || targetType == AttackLineType.NONE) {
            return context.getBoard().strongestMinionOf(context.getOpponent());
        }
        return context.getBoard().minionsOf(context.getOpponent()).stream()
            .filter(minion -> minion.getAttackLineType() == targetType)
            .max(java.util.Comparator.comparingInt(Minion::getCurrentPower))
            .orElse(null);
    }
}
