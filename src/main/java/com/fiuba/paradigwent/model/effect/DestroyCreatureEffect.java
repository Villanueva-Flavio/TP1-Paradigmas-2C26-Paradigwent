package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.Collections;

/* incompleto targetType */
public class DestroyCreatureEffect implements Effect{
    private final boolean scorch;
    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Destroys a minion";
    }

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

    public DestroyCreatureEffect(AttackLineType targetType, boolean scorch){
        this.targetType = targetType;
        this.scorch = scorch;
    }

    public boolean isScorch() {
        return scorch;
    }

    @Override 
    public void apply(GameContext context){
        Minion target = context.getTargetCard() instanceof Minion minion ? minion : null;
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
