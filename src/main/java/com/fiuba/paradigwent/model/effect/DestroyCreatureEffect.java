package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.targeting.MinionTarget;
import com.fiuba.paradigwent.model.targeting.TargetSide;
import com.fiuba.paradigwent.model.targeting.Targeting;

/**
 * Destroys a minion. When no minion was chosen, the strongest enemy minion
 * of the configured line (or of the whole side) is destroyed.
 */
public class DestroyCreatureEffect implements Effect {
    private final AttackLineType targetType;
    private final boolean scorch;

    /**
     * @param targetType line searched when no minion was chosen
     */
    public DestroyCreatureEffect(AttackLineType targetType) {
        this(targetType, false);
    }

    /**
     * @param targetType line searched when no minion was chosen
     * @param scorch     whether the effect is presented as a scorch
     */
    public DestroyCreatureEffect(AttackLineType targetType, boolean scorch) {
        this.targetType = targetType == null ? AttackLineType.NONE : targetType;
        this.scorch = scorch;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(GameContext context) {
        context.chosenTarget()
            .or(() -> context.strongestEnemyMinion(targetType))
            .ifPresent(context::destroy);
    }

    /** {@inheritDoc} */
    @Override
    public Targeting targeting() {
        String prompt = scorch ? "Select a minion to scorch" : "Select a minion to destroy";
        return new MinionTarget(prompt, false, TargetSide.ENEMY);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Destroys a minion";
    }

    /** {@inheritDoc} */
    @Override
    public String getTargetDescription() {
        return targetType == AttackLineType.NONE
            ? "Objective: A minion"
            : "Objective: A minion from the " + targetType + " line";
    }
}
