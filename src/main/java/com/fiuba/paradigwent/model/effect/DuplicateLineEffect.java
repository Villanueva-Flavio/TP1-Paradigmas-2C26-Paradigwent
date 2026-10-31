package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.targeting.LineTarget;
import com.fiuba.paradigwent.model.targeting.NoTarget;
import com.fiuba.paradigwent.model.targeting.Targeting;

/**
 * Duplicates the power of one of the lines of the player (horn).
 * With a fixed line it always acts on it; otherwise the player chooses one,
 * falling back to the line of the played card.
 */
public class DuplicateLineEffect implements Effect {
    private final AttackLineType lineType;

    /**
     * @param lineType fixed line to duplicate, or {@link AttackLineType#NONE} to let the player choose
     */
    public DuplicateLineEffect(AttackLineType lineType) {
        this.lineType = lineType == null ? AttackLineType.NONE : lineType;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(GameContext context) {
        context.duplicateLine(resolveLine(context));
    }

    private AttackLineType resolveLine(GameContext context) {
        if (lineType != AttackLineType.NONE) {
            return lineType;
        }
        return context.chosenLine().orElse(context.playedCardLine());
    }

    /** {@inheritDoc} */
    @Override
    public Targeting targeting() {
        return lineType == AttackLineType.NONE
            ? new LineTarget("Select an attack line to buff")
            : new NoTarget();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Duplicates attack line damage";
    }

    /** {@inheritDoc} */
    @Override
    public String getTargetDescription() {
        return lineType == AttackLineType.NONE
            ? "Objective: The chosen attack line"
            : "Objective: The " + lineType + " attack line";
    }
}
